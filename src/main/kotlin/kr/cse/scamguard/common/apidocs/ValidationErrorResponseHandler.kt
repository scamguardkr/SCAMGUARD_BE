package kr.cse.scamguard.common.apidocs

import io.swagger.v3.oas.models.examples.Example
import io.swagger.v3.oas.models.media.MediaType
import jakarta.validation.Valid
import kr.cse.scamguard.common.model.CommonResponse
import kr.cse.scamguard.common.model.FieldErrorResponse
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.method.HandlerMethod
import java.lang.reflect.Field

@Component
class ValidationErrorResponseHandler(
    private val schemaFactory: SchemaFactory,
    private val jsonUtil: JsonUtil
) {
    fun hasValidation(handlerMethod: HandlerMethod): Boolean {
        return handlerMethod.methodParameters.any { parameter ->
            parameter.hasParameterAnnotation(RequestBody::class.java) &&
                (parameter.hasParameterAnnotation(Valid::class.java) || parameter.hasParameterAnnotation(
                    org.springframework.validation.annotation.Validated::class.java))
        }
    }

    fun handle(mediaType: MediaType, handlerMethod: HandlerMethod) {
        if (mediaType.schema == null) {
            mediaType.schema = schemaFactory.createValidationErrorSchema()
        }

        val validationExample = buildValidationErrorExample(handlerMethod)
        mediaType.addExamples("검증 실패", Example().value(jsonUtil.prettyJson(validationExample)))
    }

    private fun buildValidationErrorExample(handlerMethod: HandlerMethod): CommonResponse<Map<String, String?>> {
        val fieldErrors = introspectValidationErrors(handlerMethod)
        return CommonResponse(
            status = "fail",
            data = null,
            errorCode = "INVALID_VALUE",
            errorMessage = "입력값이 올바르지 않습니다.",
            fieldErrors = fieldErrors
        )
    }

    private fun introspectValidationErrors(handlerMethod: HandlerMethod): List<FieldErrorResponse> {
        return handlerMethod.methodParameters
            .firstOrNull { it.hasParameterAnnotation(RequestBody::class.java) }
            ?.parameterType
            ?.let { dtoClass ->
                getAllFields(dtoClass).flatMap { field -> getValidationErrorsForField(field) }
            } ?: emptyList()
    }

    private fun getAllFields(clazz: Class<*>): List<Field> {
        val fields = mutableListOf<Field>()
        var currentClass: Class<*>? = clazz
        while (currentClass != null) {
            fields.addAll(currentClass.declaredFields)
            currentClass = currentClass.superclass
        }
        return fields
    }

    private fun getValidationErrorsForField(field: Field): List<FieldErrorResponse> {
        return field.annotations
            .filter { isValidationAnnotation(it) }
            .map { annotation ->
                FieldErrorResponse(
                    field = field.name,
                    constraint = annotation.annotationClass.simpleName ?: "Unknown",
                    message = extractCustomValidationMessage(annotation) ?: getDefaultValidationMessage(annotation)
                )
            }
    }

    private fun isValidationAnnotation(annotation: Annotation): Boolean {
        return annotation.annotationClass.annotations.any {
            it.annotationClass.qualifiedName == "jakarta.validation.Constraint"
        }
    }

    private fun extractCustomValidationMessage(annotation: Annotation): String? {
        return try {
            val messageMethod = annotation.annotationClass.java.getMethod("message")
            val message = messageMethod.invoke(annotation) as String
            // 기본 메시지 형식({javax.validation...})이 아닌 경우에만 커스텀 메시지로 간주
            if (message.isNotBlank() && !message.startsWith("{") && !message.endsWith("}")) {
                message
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun getDefaultValidationMessage(annotation: Annotation): String {
        return when (annotation.annotationClass.simpleName) {
            "NotNull" -> "null일 수 없습니다"
            "NotBlank" -> "공백일 수 없습니다"
            "NotEmpty" -> "비어있을 수 없습니다"
            "Size" -> "크기가 올바르지 않습니다"
            else -> "올바르지 않은 값입니다"
        }
    }
}

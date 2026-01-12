package kr.cse.scamguard.common.apidocs

import io.swagger.v3.oas.models.examples.Example
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import kr.cse.scamguard.common.model.CommonResponse
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.kotlinFunction

@Component
class SuccessResponseHandler(
    private val schemaFactory: SchemaFactory,
    private val exampleGenerator: ExampleGenerator,
    private val jsonUtil: JsonUtil
) {

    fun handle(mediaType: MediaType, handlerMethod: HandlerMethod) {
        val returnType = handlerMethod.method.genericReturnType

        if (mediaType.schema == null) {
            val dataSchema = getDataSchemaFromReturnType(returnType)
            mediaType.schema = schemaFactory.createSuccessSchema(dataSchema)
        }

        // 데이터가 포함된 성공 예시 생성
        val successExampleWithData = createSuccessExample(returnType)
        // null이 아닌 경우에만 성공 예시를 추가
        if (successExampleWithData.data != null) {
            mediaType.addExamples("성공", Example().value(jsonUtil.prettyJson(successExampleWithData)))
        }

        // Nullable 타입인지 확인 -> data가 null인 예시 추가
        if (isReturnTypeDataNullable(handlerMethod)) {
            val successExampleWithNullData = CommonResponse.success(null)
            val exampleName = if (successExampleWithData.data != null) "성공 (data null)" else "성공"
            mediaType.addExamples(exampleName, Example().value(jsonUtil.prettyJson(successExampleWithNullData)))
        } else if (successExampleWithData.data == null) {
            // Unit 이나 Nothing? 처럼 data가 항상 null인 경우
            mediaType.addExamples("성공", Example().value(jsonUtil.prettyJson(successExampleWithData)))
        }
    }

    /**
     * 코틀린 리플렉션을 사용하여 반환 타입의 제네릭 인수가 Nullable인지 확인
     */
    private fun isReturnTypeDataNullable(handlerMethod: HandlerMethod): Boolean {
        val kFunction = handlerMethod.method.kotlinFunction ?: return false

        val returnKType = kFunction.returnType
        val returnClass = (returnKType.classifier as? kotlin.reflect.KClass<*>)
        if (returnClass == null || !returnClass.isSubclassOf(CommonResponse::class)) {
            return false
        }

        val typeArgument = returnKType.arguments.firstOrNull()?.type ?: return false
        return typeArgument.isMarkedNullable
    }

    private fun createSuccessExample(returnType: Type): CommonResponse<*> {
        val dataExample = exampleGenerator.generateExample(returnType, isSuccessResponse = true)
        return CommonResponse.success(dataExample)
    }

    private fun getDataSchemaFromReturnType(returnType: Type): Schema<*>? {
        if (returnType is ParameterizedType && returnType.rawType == CommonResponse::class.java) {
            val actualType = returnType.actualTypeArguments[0]
            return exampleGenerator.getSchemaForType(actualType)
        }
        return null
    }

}

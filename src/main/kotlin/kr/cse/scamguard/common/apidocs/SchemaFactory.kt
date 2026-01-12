package kr.cse.scamguard.common.apidocs

import io.swagger.v3.oas.models.media.Schema
import kr.cse.scamguard.common.model.CommonResponse
import org.springframework.stereotype.Component

@Component
class SchemaFactory {

    fun createSuccessSchema(dataSchema: Schema<*>?): Schema<*> {
        return Schema<CommonResponse<*>>().apply {
            addProperty("status", Schema<String>()._enum(listOf("success")))
            addProperty("data", dataSchema ?: Schema<Any>().apply { nullable = true })
            addProperty("errorCode", Schema<String>().apply { nullable = true })
            addProperty("errorMessage", Schema<String>().apply { nullable = true })
            addProperty("fieldErrors", Schema<List<Any>>().apply { nullable = true })
        }
    }

    fun createErrorSchema(): Schema<*> {
        return Schema<CommonResponse<*>>().apply {
            type = "object"
            addProperty("status", Schema<String>()._enum(listOf("error")))
            addProperty("data", Schema<Any>().apply { nullable = true })
            addProperty("errorCode", Schema<String>())
            addProperty("errorMessage", Schema<String>())
            addProperty("fieldErrors", Schema<List<Any>>().apply { nullable = true })
        }
    }

    fun createValidationErrorSchema(): Schema<*> {
        val fieldErrorSchema = Schema<Any>().apply {
            type = "object"
            addProperty("field", Schema<String>())
            addProperty("constraint", Schema<String>())
            addProperty("message", Schema<String>())
        }

        return Schema<CommonResponse<*>>().apply {
            type = "object"
            addProperty("status", Schema<String>()._enum(listOf("fail")))
            addProperty("data", Schema<Any>().apply { nullable = true })
            addProperty("errorCode", Schema<String>().example("INVALID_VALUE"))
            addProperty("errorMessage", Schema<String>().example("입력값이 올바르지 않습니다."))
            addProperty("fieldErrors", Schema<List<Any>>().apply {
                items = fieldErrorSchema
            })
        }
    }
}

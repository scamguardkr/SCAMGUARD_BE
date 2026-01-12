package kr.cse.scamguard.common.apidocs

import io.swagger.v3.oas.models.examples.Example
import io.swagger.v3.oas.models.media.MediaType
import kr.cse.scamguard.common.exception.BaseResponseCode
import kr.cse.scamguard.common.model.toErrorResponse
import org.springframework.stereotype.Component

@Component
class ErrorResponseHandler(
    private val schemaFactory: SchemaFactory,
    private val jsonUtil: JsonUtil
) {
    fun handle(mediaType: MediaType, code: BaseResponseCode) {
        if (mediaType.schema == null) {
            mediaType.schema = schemaFactory.createErrorSchema()
        }
        val errorExample = code.toErrorResponse()
        mediaType.addExamples(code.message, Example().value(jsonUtil.prettyJson(errorExample)))
    }
}


package kr.cse.scamguard.common.apidocs

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Component

@Component
class JsonUtil(
    private val objectMapper: ObjectMapper = jacksonObjectMapper().findAndRegisterModules()
) {
    fun prettyJson(obj: Any): String {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj)
    }
}

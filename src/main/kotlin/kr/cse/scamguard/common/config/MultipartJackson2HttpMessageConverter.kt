package kr.cse.scamguard.common.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter
import org.springframework.stereotype.Component

/**
 * Swagger + RequestPart를 통해 파일, Dto 동시 요청 시 발생 에러 해결
 * {
 *   "type": "about:blank",
 *   "title": "Unsupported Media Type",
 *   "status": 415,
 *   "detail": "Content-Type 'application/octet-stream' is not supported.",
 *   "instance": "/api/v1/switch/2/review"
 * }
 * application/octet-stream 타입을 담당할 컨버터를 따로 추가하여 해당 컨버터를 가지고 정상적으로 JSON body를 변환할 수 있도록 함
 * */
@Component
class MultipartJackson2HttpMessageConverter(
    private val objectMapper: ObjectMapper
): AbstractJackson2HttpMessageConverter(objectMapper, MediaType.APPLICATION_OCTET_STREAM) {

    override fun canWrite(mediaType: MediaType?): Boolean {
        return false
    }
}

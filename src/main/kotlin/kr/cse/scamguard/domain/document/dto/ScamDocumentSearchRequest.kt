package kr.cse.scamguard.domain.document.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * 유사도 검색 요청 DTO (테스트용)
 */
@Schema(description = "유사도 검색 요청 (테스트용)")
data class ScamDocumentSearchRequest(
    
    @field:Schema(
        description = "검색 쿼리 문장",
        example = "이상한 전화가 와서 계좌번호를 알려달라고 합니다",
        required = true
    )
    val query: String,
    
    @field:Schema(
        description = "반환할 결과 수 (기본값: 10)",
        example = "10",
        minimum = "1",
        maximum = "100"
    )
    val topK: Int = 10
)

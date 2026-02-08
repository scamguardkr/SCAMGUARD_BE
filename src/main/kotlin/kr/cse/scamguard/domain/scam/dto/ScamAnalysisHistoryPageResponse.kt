package kr.cse.scamguard.domain.scam.dto

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.domain.Page

@Schema(description = "사기 분석 이력 페이징 응답")
data class ScamAnalysisHistoryPageResponse(

    @field:Schema(description = "분석 내역 목록")
    val contents: List<ScamAnalysisHistoryListResponse>,

    @field:Schema(description = "현재 페이지 번호 (0-based)")
    val page: Int,

    @field:Schema(description = "페이지당 항목 수")
    val size: Int,

    @field:Schema(description = "총 항목 수")
    val totalElements: Long,

    @field:Schema(description = "총 페이지 수")
    val totalPages: Int,

    @field:Schema(description = "마지막 페이지 여부")
    val last: Boolean
) {
    companion object {
        fun <T> of(page: Page<T>, mapper: (T) -> ScamAnalysisHistoryListResponse): ScamAnalysisHistoryPageResponse {
            return ScamAnalysisHistoryPageResponse(
                contents = page.content.map(mapper),
                page = page.number,
                size = page.size,
                totalElements = page.totalElements,
                totalPages = page.totalPages,
                last = page.isLast
            )
        }
    }
}

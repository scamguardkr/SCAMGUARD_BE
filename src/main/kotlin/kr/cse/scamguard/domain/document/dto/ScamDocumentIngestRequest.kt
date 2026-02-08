package kr.cse.scamguard.domain.document.dto

import io.swagger.v3.oas.annotations.media.Schema
import kr.cse.scamguard.domain.document.model.ScamType

/**
 * 사기 문서 수집 요청 DTO
 */
@Schema(description = "사기 문서 수집 요청")
data class ScamDocumentIngestRequest(
    
    @field:Schema(
        description = "사기 사건 제목",
        example = "중고거래 플랫폼 입금 후 미배송 사기",
        required = true
    )
    val scamTitle: String,
    
    @field:Schema(
        description = "사기 유형",
        example = "SHOPPING_FRAUD",
        required = true
    )
    val scamType: ScamType,
    
    @field:Schema(
        description = "사기 발생 시기",
        example = "2026년 2월"
    )
    val occurredPeriod: String?,
    
    @field:Schema(
        description = "사기 수법 요약 (벡터화 대상)",
        example = "네이버 중고나라에서 정가 대비 현저히 저렴한 가격(120만원→65만원)의 미개봉 전자기기를 미끼로 판매글 게시...",
        required = true
    )
    val scamMethodSummary: String,
    
    @field:Schema(
        description = "원본 출처 URL",
        example = "https://n.news.naver.com/mnews/article/052/0002311584?sid=102"
    )
    val sourceUrl: String?
)

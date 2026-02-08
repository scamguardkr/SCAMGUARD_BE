package kr.cse.scamguard.domain.document.model

/**
 * 사기 문서의 출처 유형
 */
enum class ScamSourceType {
    NEWS,         // 뉴스 기사
    COMMUNITY,    // 커뮤니티 게시글
    POLICE,       // 경찰 발표
    USER_REPORT   // 사용자 보고
}

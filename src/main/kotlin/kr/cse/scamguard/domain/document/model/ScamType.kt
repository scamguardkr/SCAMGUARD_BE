package kr.cse.scamguard.domain.document.model

/**
 * 사기 유형 분류
 */
enum class ScamType {
    VOICE_PHISHING,     // 보이스피싱
    SMISHING,           // 스미싱 (문자 사기)
    PHISHING,           // 피싱 (이메일/웹 사기)
    INVESTMENT_FRAUD,   // 투자 사기
    ROMANCE_SCAM,       // 로맨스 스캠
    IMPERSONATION,      // 사칭 (기관/지인 사칭)
    EMPLOYMENT_SCAM,    // 채용/알바 사기
    SHOPPING_FRAUD,     // 쇼핑/거래 사기
    OTHER               // 기타
}

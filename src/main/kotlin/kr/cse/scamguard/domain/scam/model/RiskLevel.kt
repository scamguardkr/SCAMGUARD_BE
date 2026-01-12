package kr.cse.scamguard.domain.scam.model

enum class RiskLevel(val description: String) {
    NONE("없음"),
    LOW("낮음"),
    SUSPICIOUS("의심"),
    DANGEROUS("위험"),
    VERY_DANGEROUS("매우 위험")
}

package kr.cse.scamguard.domain.document.model

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import org.springframework.data.elasticsearch.annotations.Setting
import java.time.LocalDate

/**
 * Elasticsearch에 저장되는 문서 청크 엔티티
 * 
 * 하나의 원본 문서는 여러 청크로 분할되어 저장됩니다.
 * 각 청크는 자체 벡터 임베딩을 가지며, 검색 시 벡터 유사도로 검색됩니다.
 * 
 * RAG 호환성 주의:
 * - embeddingModel 필드에 임베딩 생성 시 사용한 모델명이 저장됩니다.
 * - 검색 시 동일한 모델로 쿼리 벡터를 생성해야 정확한 결과를 얻을 수 있습니다.
 */
@Document(indexName = "scam_document_chunks")
@Setting(shards = 1, replicas = 0)
data class ScamDocumentChunk(
    
    @Id
    val id: String? = null,
    
    /**
     * 원본 문서 ID (같은 문서에서 나온 청크들은 동일한 documentId를 가짐)
     */
    @Field(type = FieldType.Keyword)
    val documentId: String,
    
    /**
     * 청크 인덱스 (0부터 시작)
     */
    @Field(type = FieldType.Integer)
    val chunkIndex: Int,
    
    /**
     * 청크 텍스트 내용 (scamMethodSummary에서 추출)
     */
    @Field(type = FieldType.Text, analyzer = "standard")
    val content: String,
    
    /**
     * 벡터 임베딩
     * dims는 사용하는 임베딩 모델에 따라 달라짐
     */
    @Field(type = FieldType.Dense_Vector, dims = 3072)
    val embedding: FloatArray,
    
    /**
     * 임베딩 생성에 사용된 모델명
     * 검색 시 호환성 체크에 사용
     */
    @Field(type = FieldType.Keyword)
    val embeddingModel: String,
    
    // ===== 원본 문서 메타데이터 =====
    
    /**
     * 사기 사건 제목
     */
    @Field(type = FieldType.Text)
    val scamTitle: String,
    
    /**
     * 사기 유형
     */
    @Field(type = FieldType.Keyword)
    val scamType: ScamType,
    
    /**
     * 사기 발생 시기
     */
    @Field(type = FieldType.Text)
    val occurredPeriod: String?,
    
    /**
     * 원본 출처 URL
     */
    @Field(type = FieldType.Keyword)
    val sourceUrl: String?,
    
    /**
     * 문서 저장 날짜
     */
    @Field(type = FieldType.Date)
    val createdAt: LocalDate? = null
) {
    // FloatArray 비교를 위한 equals/hashCode 오버라이드
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ScamDocumentChunk
        return id == other.id && documentId == other.documentId && chunkIndex == other.chunkIndex
    }
    
    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + documentId.hashCode()
        result = 31 * result + chunkIndex
        return result
    }
}

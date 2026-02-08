package kr.cse.scamguard.domain.document.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.cse.scamguard.common.embedding.EmbeddingModelInfo
import kr.cse.scamguard.common.model.CommonResponse
import kr.cse.scamguard.common.model.toSuccessResponse
import kr.cse.scamguard.domain.document.dto.ScamDocumentIngestRequest
import kr.cse.scamguard.domain.document.dto.ScamDocumentIngestResponse
import kr.cse.scamguard.domain.document.dto.ScamDocumentSearchRequest
import kr.cse.scamguard.domain.document.dto.ScamDocumentSearchResponse
import kr.cse.scamguard.domain.document.service.ScamDocumentIngestService
import kr.cse.scamguard.domain.document.service.ScamDocumentSearchService
import org.slf4j.LoggerFactory
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * 문서 수집 및 검색 컨트롤러
 * 
 * 사기 관련 문서를 벡터화하여 Elasticsearch에 저장하고 검색합니다.
 */
@RestController
class ScamDocumentController(
    private val ingestService: ScamDocumentIngestService,
    private val searchService: ScamDocumentSearchService
) : ScamDocumentApi {
    
    private val log = LoggerFactory.getLogger(this::class.java)
    
    /**
     * 문서 수집 API
     */
    @PostMapping("/api/v1/scam/documents")
    @PreAuthorize("hasRole('ADMIN')")
    override fun ingestDocument(
        @RequestBody request: ScamDocumentIngestRequest,
        @AuthenticationPrincipal user: UserDetails
    ): CommonResponse<ScamDocumentIngestResponse> {
        log.info("문서 수집 요청 - 사용자: {}", user.username)
        return ingestService.ingestDocument(request).toSuccessResponse()
    }
    
    /**
     * 현재 활성 임베딩 모델 정보 조회
     */
    @GetMapping("/api/v1/scam/documents/embedding-model")
    override fun getActiveEmbeddingModel(): CommonResponse<EmbeddingModelInfo> {
        return ingestService.getActiveEmbeddingModelInfo().toSuccessResponse()
    }
    
    /**
     * 문서 삭제 (documentId로 해당 문서의 모든 청크 삭제)
     */
    @DeleteMapping("/api/v1/scam/documents/{documentId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteDocument(
        @PathVariable documentId: String,
        @AuthenticationPrincipal user: UserDetails
    ): CommonResponse<Nothing?> {
        log.info("문서 삭제 요청 - 사용자: {}, documentId: {}", user.username, documentId)
        ingestService.deleteDocument(documentId)
        return CommonResponse.success()
    }
    
    /**
     * 유사도 검색 API (테스트용)
     * 
     * 입력 문장을 벡터화하여 저장된 문서 청크와 코사인 유사도를 계산합니다.
     */
    @PostMapping("/api/v1/scam/documents/search")
    @Operation(
        summary = "유사도 검색 (테스트용)",
        description = """
            쿼리 문장을 현재 활성 임베딩 모델로 벡터화하고,
            저장된 문서 청크와의 코사인 유사도를 계산하여 반환합니다.
            
            - 점수 범위: 0~1 (1에 가까울수록 유사)
            - 동일한 임베딩 모델로 저장된 문서만 정확한 비교 가능
        """
    )
    fun searchSimilarDocuments(
        @RequestBody request: ScamDocumentSearchRequest
    ): CommonResponse<ScamDocumentSearchResponse> {
        log.info("유사도 검색 요청 - 쿼리: {}", request.query.take(50))
        return searchService.search(request).toSuccessResponse()
    }
}


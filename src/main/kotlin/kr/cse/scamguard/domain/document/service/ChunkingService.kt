package kr.cse.scamguard.domain.document.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service


/**
 * 텍스트 청킹 서비스
 * 
 * 긴 문서를 일정 크기의 청크로 분할합니다.
 * 청크 간 오버랩을 두어 문맥이 끊기는 것을 방지합니다.
 * 
 * TODO [비동기 처리 가이드]
 * 대용량 문서의 청킹이 필요한 경우:
 * - Kotlin Flow를 사용하여 청크를 스트리밍 방식으로 처리
 * - 또는 parallelStream으로 병렬 처리
 */
@Service
class ChunkingService(
    @Value("\${app.embedding.chunk-size}") private val chunkSize: Int,
    @Value("\${app.embedding.chunk-overlap}") private val chunkOverlap: Int
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * 텍스트를 청크로 분할
     *
     * @param text 원본 텍스트
     * @return 청크 리스트
     */
    fun chunk(text: String): List<String> {
        val cleanedText = cleanText(text)

        log.debug("청크 분할 시작 - 텍스트 길이: {}, 청크 크기: {}, 오버랩: {}", cleanedText.length, chunkSize, chunkOverlap)

        // 텍스트가 청크 크기보다 작으면 그대로 반환
        if (cleanedText.length <= chunkSize) {
            log.debug("텍스트가 청크 크기보다 작아 분할하지 않음")
            return listOf(cleanedText)
        }

        val chunks = mutableListOf<String>()
        var startIndex = 0
        var iteration = 0
        val maxIterations = cleanedText.length // 안전장치

        while (startIndex < cleanedText.length) {
            iteration++

            // 무한 루프 감지
            if (iteration > maxIterations) {
                log.error("무한 루프 감지! iteration: {}, startIndex: {}, textLength: {}", iteration, startIndex, cleanedText.length)
                throw IllegalStateException("청크 분할 중 무한 루프 감지 (iteration: $iteration)")
            }

            // 주기적으로 진행 상황 로깅
            if (iteration % 100 == 0) {
                log.warn("청크 분할 진행 중 - iteration: {}, startIndex: {}/{}", iteration, startIndex, cleanedText.length)
            }

            var endIndex = minOf(startIndex + chunkSize, cleanedText.length)
            val originalEndIndex = endIndex

            // 마지막 청크가 아닐 때만 breakpoint 찾기
            if (endIndex < cleanedText.length) {
                val betterBreakPoint = findBetterBreakPoint(cleanedText, endIndex)

                log.trace("breakpoint 검색 - original: {}, better: {}, startIndex: {}", originalEndIndex, betterBreakPoint, startIndex)

                // breakpoint가 현재 startIndex보다 뒤에 있을 때만 적용
                if (betterBreakPoint > startIndex) {
                    endIndex = betterBreakPoint
                } else {
                    log.warn(
                        "부적절한 breakpoint 감지 - betterBreakPoint({}) <= startIndex({}), 원래 endIndex({}) 유지",
                        betterBreakPoint, startIndex, originalEndIndex
                    )
                }
            }

            val chunk = cleanedText.substring(startIndex, endIndex).trim()

            log.trace("청크 생성 - startIndex: {}, endIndex: {}, 길이: {}", startIndex, endIndex, chunk.length)

            // 청크가 비어있지 않으면 추가
            if (chunk.isNotEmpty()) {
                // 마지막 청크가 너무 짧으면 이전 청크에 병합
                if (chunk.length < chunkOverlap && chunks.isNotEmpty()) {
                    val lastChunk = chunks.removeLast()
                    chunks.add("$lastChunk $chunk")
                    log.debug("짧은 청크를 이전 청크에 병합 - 청크 길이: {}", chunk.length)
                } else {
                    chunks.add(chunk)
                    log.debug("청크 추가 - 총 청크 수: {}, 청크 길이: {}", chunks.size, chunk.length)
                }
            } else {
                log.warn("빈 청크 감지 - startIndex: {}, endIndex: {}", startIndex, endIndex)
            }

            // 다음 시작점 계산 (최소한 1은 전진)
            val nextStart = maxOf(startIndex + 1, endIndex - chunkOverlap)

            log.trace("다음 시작점 계산 - current: {}, next: {}, endIndex: {}", startIndex, nextStart, endIndex)

            // 진행하지 못하는 경우 경고
            if (nextStart <= startIndex) {
                log.error(
                    "startIndex가 전진하지 않음! current: {}, next: {}, endIndex: {}, chunkOverlap: {}",
                    startIndex, nextStart, endIndex, chunkOverlap
                )
            }

            // 더 이상 진행할 수 없으면 종료
            if (nextStart >= cleanedText.length) {
                log.debug("텍스트 끝에 도달 - 청크 분할 완료")
                break
            }

            startIndex = nextStart
        }

        log.info("청크 분할 완료 - 총 청크 수: {}, 반복 횟수: {}", chunks.size, iteration)

        return chunks
    }

    /**
     * 텍스트 정제
     * - 연속된 공백 제거
     * - 특수 문자 정규화
     */
    private fun cleanText(text: String): String {
        return text
            .replace(Regex("\\s+"), " ")  // 연속 공백을 단일 공백으로
            .replace(Regex("[\\r\\n]+"), " ")  // 줄바꿈을 공백으로
            .trim()
    }

    /**
     * 더 나은 끊김점 찾기
     * 가능하면 문장이나 단어 경계에서 끊도록 함
     */
    private fun findBetterBreakPoint(text: String, targetIndex: Int): Int {
        // 목표 인덱스 근처에서 문장 종결 부호 찾기 (뒤에서부터)
        val searchRange = maxOf(0, targetIndex - 50)..targetIndex

        // 문장 종결 부호 찾기
        for (i in targetIndex downTo searchRange.first) {
            val char = text[i]
            // 한국어 문장 종결 부호 또는 영어 구두점
            if (char in listOf('.', '!', '?', '。', '!', '?')) {
                log.trace("문장 종결 부호에서 끊김 - index: {}, char: '{}'", i, char)
                return i + 1
            }
        }

        // 문장 종결을 못 찾으면 공백에서 끊기
        for (i in targetIndex downTo searchRange.first) {
            if (text[i].isWhitespace()) {
                log.trace("공백에서 끊김 - index: {}", i)
                return i + 1
            }
        }

        // 아무것도 못 찾으면 원래 인덱스 반환
        log.trace("적절한 끊김점을 찾지 못함 - 원래 인덱스 반환: {}", targetIndex)
        return targetIndex
    }
}

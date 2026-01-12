package kr.cse.scamguard.common.model

import kotlin.math.ceil

data class Criteria private constructor(
    val page: Int,
    val limit: Int
) {
    companion object {
        const val DEFAULT_PAGE = 1
        private const val MIN_PAGE = 1

        const val DEFAULT_LIMIT = 10
        private const val MIN_LIMIT = 1
        private const val MAX_LIMIT = 50

        fun of(page: Int? = null, limit: Int? = null): Criteria {
            return Criteria(
                page = validateAndCalculatePage(page),
                limit = validateAndCalculateLimit(limit)
            )
        }

        fun of(page: Int? = null, limit: Int? = null, total: Int): Criteria {
            val validatedLimit = validateAndCalculateLimit(limit)
            return Criteria(
                page = validateAndCalculatePage(page, validatedLimit, total),
                limit = validatedLimit
            )
        }

        private fun validateAndCalculatePage(page: Int?): Int {
            var validatedPage = page ?: DEFAULT_PAGE
            if (validatedPage < MIN_PAGE) {
                validatedPage = MIN_PAGE
            }
            return validatedPage - 1
        }

        private fun validateAndCalculatePage(page: Int?, limit: Int, total: Int): Int {
            val totalPage = if (total == 0) 1 else ceil(total.toDouble() / limit).toInt()

            var validatedPage = page ?: DEFAULT_PAGE
            if (validatedPage < MIN_PAGE) {
                validatedPage = MIN_PAGE
            }
            if (validatedPage > totalPage) {
                validatedPage = totalPage
            }
            return validatedPage - 1
        }

        private fun validateAndCalculateLimit(limit: Int?): Int {
            return when {
                limit == null -> DEFAULT_LIMIT
                limit < MIN_LIMIT -> MIN_LIMIT
                limit > MAX_LIMIT -> MAX_LIMIT
                else -> limit
            }
        }
    }
}

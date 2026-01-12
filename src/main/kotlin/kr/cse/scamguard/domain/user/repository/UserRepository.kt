package kr.cse.scamguard.domain.user.repository

import kr.cse.scamguard.common.exception.CustomException
import kr.cse.scamguard.domain.user.exception.UserErrorCode
import kr.cse.scamguard.domain.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull

interface UserRepository : JpaRepository<User, Long> {

    fun existsUserByLoginId(loginId: String): Boolean
    fun findUserByLoginId(loginId: String): User?
}

fun UserRepository.getByIdOrThrow(id: Long): User {
    return this.findByIdOrNull(id)
        ?: throw CustomException(UserErrorCode.NOT_FOUND_USER)
}

fun UserRepository.getByLoginIdOrThrow(loginId: String): User {
    return this.findUserByLoginId(loginId)
        ?: throw CustomException(UserErrorCode.NOT_FOUND_USER)
}

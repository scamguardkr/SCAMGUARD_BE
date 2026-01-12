package kr.cse.scamguard.domain.user.service

import kr.cse.scamguard.domain.user.dto.SingleUserProfileResponse
import kr.cse.scamguard.domain.user.repository.UserRepository
import kr.cse.scamguard.domain.user.repository.getByIdOrThrow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService (
    private val userRepository: UserRepository
) {

    @Transactional(readOnly = true)
    fun getProfile(userId: Long): SingleUserProfileResponse {
        return SingleUserProfileResponse.from(userRepository.getByIdOrThrow(userId))
    }
}

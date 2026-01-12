package kr.cse.scamguard.domain.user.service

import kr.cse.scamguard.common.security.jwt.JwtAuthManager
import kr.cse.scamguard.domain.user.dto.UserRegisterCommand
import kr.cse.scamguard.domain.user.dto.UserRegisterResponse
import kr.cse.scamguard.domain.user.model.User
import kr.cse.scamguard.domain.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserRegisterService (
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtAuthManager: JwtAuthManager
) {

    fun execute(command: UserRegisterCommand): UserRegisterResponse {
        val savedUser = createAndSaveUser(command)
        val tokens = jwtAuthManager.createToken(savedUser)
        return UserRegisterResponse.from(savedUser.id, tokens.accessToken, tokens.refreshToken)
    }

    private fun createAndSaveUser(command: UserRegisterCommand): User {
        val user = command.toEntity()
        user.encodePassword(passwordEncoder.encode(command.loginPw))
        return userRepository.save(user)
    }
}

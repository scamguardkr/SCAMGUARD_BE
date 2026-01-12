package kr.cse.scamguard.domain.user.service

import kr.cse.scamguard.common.security.jwt.JwtAuthManager
import kr.cse.scamguard.domain.user.dto.UserLoginCommand
import kr.cse.scamguard.domain.user.dto.UserLoginResponse
import kr.cse.scamguard.domain.user.repository.UserRepository
import kr.cse.scamguard.domain.user.repository.getByLoginIdOrThrow
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserLoginService (
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtAuthManager: JwtAuthManager,
) {

    fun execute(command: UserLoginCommand) : UserLoginResponse {
        val user = userRepository.getByLoginIdOrThrow(command.loginId)
        user.validatePassword(passwordEncoder, command.loginPw)

        val tokens = jwtAuthManager.createToken(user)

        return UserLoginResponse.from(user.id, tokens.accessToken, tokens.refreshToken)
    }
}

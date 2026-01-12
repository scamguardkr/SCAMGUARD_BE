package kr.cse.scamguard.domain.user.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import kr.cse.scamguard.domain.user.model.User
import kr.cse.scamguard.domain.user.model.UserType

object UserRegisterDto {

    data class UserRegisterRequest(
        @field:Schema(description = "아이디", example = "testuser")
        @field:NotBlank
        val loginId: String,

        @field:Schema(description = "비밀번호", example = "1q2w3e4r!")
        @field:NotBlank
        val loginPw: String,

        @field:Schema(description = "이름", example = "홍길동", maxLength = 255)
        @field:NotBlank
        val name: String,

        @field:Schema(description = "이메일", example = "scamguardOfficial@naver.com", maxLength = 255)
        @field:NotBlank
        val email: String,
    ) {
        fun toCommand(): UserRegisterCommand {
            return UserRegisterCommand(
                loginId = this.loginId,
                loginPw = this.loginPw,
                name = this.name,
                email = this.email
            )
        }
    }

    data class UserRegisterCommand(
        val loginId: String,
        val loginPw: String,
        val name: String,
        val email: String
    ) {
        fun toEntity(): User {
            return User(
                loginId = this.loginId,
                loginPw = this.loginPw,
                name = this.name,
                email = this.email,
                type = UserType.MEMBER,
            )
        }
    }

    data class UserRegisterResponse(
        @field:Schema(description = "사용자 식별자", example = "1")
        @field:NotBlank
        val userId: Long?,

        @field:Schema(description = "엑세스 토큰")
        @field:NotBlank
        val accessToken: String,

        @field:Schema(description = "리프레시 토큰")
        @field:NotBlank
        val refreshToken: String
    ) {
        companion object {
            fun from(userId: Long?, accessToken: String, refreshToken: String): UserRegisterResponse {
                return UserRegisterResponse(userId, accessToken, refreshToken)
            }
        }
    }
}

typealias UserRegisterRequest = UserRegisterDto.UserRegisterRequest
typealias UserRegisterCommand = UserRegisterDto.UserRegisterCommand
typealias UserRegisterResponse = UserRegisterDto.UserRegisterResponse

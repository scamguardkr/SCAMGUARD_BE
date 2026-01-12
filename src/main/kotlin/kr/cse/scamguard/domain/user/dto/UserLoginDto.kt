package kr.cse.scamguard.domain.user.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

object UserLoginDto {

    data class UserLoginRequest (
        @field:Schema(description = "아이디")
        @field:NotBlank
        val loginId: String,
        @field:Schema(description = "비밀번호")
        @field:NotBlank
        val loginPw: String,
    ) {
        fun toCommand(): UserLoginCommand {
            return UserLoginCommand(
                loginId = this.loginId,
                loginPw = this.loginPw
            )
        }
    }

    data class UserLoginCommand (
        val loginId: String,
        val loginPw: String
    )

    data class UserLoginResponse (
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
            fun from(userId: Long?, accessToken: String, refreshToken: String): UserLoginResponse {
                return UserLoginResponse(userId, accessToken, refreshToken)
            }
        }
    }

    data class UserAuthStateResponse(
        @field:Schema(description = "사용자 식별자", example = "1")
        @field:NotBlank
        val userId: Long?
    )

    data class UserAuthRefreshResponse(
        @field:Schema(description = "사용자 식별자", example = "1")
        @field:NotBlank
        val userId: Long?,

        @field:Schema(description = "엑세스 토큰")
        @field:NotBlank
        val accessToken: String,

        @field:Schema(description = "리프레시 토큰")
        @field:NotBlank
        val refreshToken: String
    )
}

typealias UserLoginRequest = UserLoginDto.UserLoginRequest
typealias UserLoginCommand = UserLoginDto.UserLoginCommand
typealias UserLoginResponse = UserLoginDto.UserLoginResponse
typealias UserAuthStateResponse = UserLoginDto.UserAuthStateResponse
typealias UserAuthRefreshResponse = UserLoginDto.UserAuthRefreshResponse

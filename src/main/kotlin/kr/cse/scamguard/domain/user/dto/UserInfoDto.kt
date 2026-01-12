package kr.cse.scamguard.domain.user.dto

import io.swagger.v3.oas.annotations.media.Schema
import kr.cse.scamguard.domain.user.model.User

object UserInfoDto {

    data class SingleUserResponse(
        @field:Schema(description = "사용자 식별자", example = "1")
        val userId: Long?,

        @field:Schema(description = "사용자 이름", example = "홍길동")
        val name: String,

        @field:Schema(description = "사용자 이메일", example = "scamguardOfficial@naver.com")
        val email: String,
    ) {
        companion object {
            fun from(user: User): SingleUserResponse {
                return SingleUserResponse(
                    userId = user.id,
                    name = user.name,
                    email = user.email
                )
            }
        }
    }

    data class SingleUserProfileResponse(
        @field:Schema(description = "사용자 식별자", example = "1")
        val userId: Long?,

        @field:Schema(description = "사용자 이메일", example = "test@naver.com")
        val userEmail: String,

        @field:Schema(description = "사용자 닉네임", example = "홍길동")
        val name: String,

        @field:Schema(description = "사용자 권한", example = "ADMIN")
        val role: String
    ) {
        companion object {
            fun from(user: User): SingleUserProfileResponse {
                return SingleUserProfileResponse(
                    userId = user.id,
                    name = user.name,
                    role = user.type.toString(),
                    userEmail = user.email
                )
            }
        }
    }
}

typealias SingleUserResponse = UserInfoDto.SingleUserResponse
typealias SingleUserProfileResponse = UserInfoDto.SingleUserProfileResponse

package kr.cse.scamguard.domain.user.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import kr.cse.scamguard.common.apidocs.ApiResponseCodes
import kr.cse.scamguard.common.model.CommonResponse
import kr.cse.scamguard.common.security.SecurityUserDetails
import kr.cse.scamguard.domain.user.dto.*
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@Tag(name = "(Normal) Auth: 사용자 인증", description = "인증을 관리한다")
interface AuthApi {

    @ApiResponseCodes(value = [
        "CommonSuccessCode.OK"
    ])
    @Operation(
        summary = "회원가입",
        description = "<h3>회원가입</h3><ul><li>회원가입 합니다</li></ul>"
    )
    @PostMapping("/api/v1/auth/join")
    fun register(@RequestBody @Valid request: UserRegisterRequest): CommonResponse<UserRegisterResponse>

    @ApiResponseCodes(value = [
        "CommonSuccessCode.OK",
        "UserErrorCode.INVALID_PASSWORD"
    ])
    @Operation(
        summary = "로그인",
        description = "<h3>로그인</h3><ul><li>로그인 합니다</li></ul>"
    )
    @PostMapping("/api/v1/auth/login")
    fun login(@Valid @RequestBody request: UserLoginRequest): CommonResponse<UserLoginResponse>

    @ApiResponseCodes(value = [
        "CommonSuccessCode.OK",
        "UserErrorCode.NOT_FOUND_USER",
        "CommonErrorCode.MISSING_OR_INVALID_AUTHENTICATION_CREDENTIALS",
        "CommonErrorCode.ACCESS_TO_THE_REQUESTED_RESOURCE_IS_FORBIDDEN",
        "JwtErrorCode.FAILED_AUTHENTICATION",
        "JwtErrorCode.EXPIRED_TOKEN",
        "JwtErrorCode.MALFORMED_TOKEN",
        "JwtErrorCode.TAMPERED_TOKEN"
    ])
    @Operation(
        summary = "로그아웃",
        description = "<h3>로그아웃</h3><ul><li>accessToken과 refreshToken 을 모두 해제합니다.</li></ul>"
    )
    @PostMapping("/api/v1/auth/logout")
    @PreAuthorize("isAuthenticated()")
    fun logout(
        @Parameter(hidden = true) @RequestHeader(value = "Authorization") authHeader: String,
        @AuthenticationPrincipal user: SecurityUserDetails
    ): CommonResponse<Nothing?>

    @ApiResponseCodes(value = [
        "CommonSuccessCode.OK",
        "UserErrorCode.NOT_FOUND_USER",
        "CommonErrorCode.MISSING_OR_INVALID_AUTHENTICATION_CREDENTIALS",
        "CommonErrorCode.ACCESS_TO_THE_REQUESTED_RESOURCE_IS_FORBIDDEN",
        "JwtErrorCode.FAILED_AUTHENTICATION",
        "JwtErrorCode.EXPIRED_TOKEN",
        "JwtErrorCode.MALFORMED_TOKEN",
        "JwtErrorCode.TAMPERED_TOKEN",
        "JwtErrorCode.FORBIDDEN_ACCESS_TOKEN"
    ])
    @Operation(
        summary = "로그인 상태 확인",
        description = "<h3>로그인 상태 확인</h3><ul><li>사용자의 로그인 상태를 확인하고 토큰에 등록된 사용자 pk값을 확인합니다</li></ul>"
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/api/v1/auth")
    fun readAuthState(
        @Parameter(hidden = true)
        @RequestHeader(value = "Authorization", required = false, defaultValue = "")
        authHeader: String
    ): CommonResponse<UserAuthStateResponse>

    @ApiResponseCodes(value = [
        "CommonSuccessCode.OK"
    ])
    @Operation(
        summary = "토큰 갱신",
        description = "<h3>토큰 갱신</h3><ul><li>토큰을 갱신합니다.</li></ul>"
    )
    @PostMapping("/api/v1/auth/refresh")
    fun refresh(@RequestParam refreshToken: String): CommonResponse<UserAuthRefreshResponse>

    @ApiResponseCodes(value = [
        "CommonSuccessCode.OK",
        "UserErrorCode.NOT_FOUND_USER",
        "CommonErrorCode.MISSING_OR_INVALID_AUTHENTICATION_CREDENTIALS",
        "CommonErrorCode.ACCESS_TO_THE_REQUESTED_RESOURCE_IS_FORBIDDEN",
        "JwtErrorCode.FAILED_AUTHENTICATION",
        "JwtErrorCode.EXPIRED_TOKEN",
        "JwtErrorCode.MALFORMED_TOKEN",
        "JwtErrorCode.TAMPERED_TOKEN",
        "JwtErrorCode.FORBIDDEN_ACCESS_TOKEN"
    ])
    @Operation(
        summary = "사용자 정보 조회",
        description = "<h3>사용자 정보 확인</h3><ul><li>사용자의 정보(닉네임, 권한, 식별자)를 조회합니다</li></ul>"
    )
    @GetMapping("/api/v1/auth/profile")
    @PreAuthorize("isAuthenticated()")
    fun getProfile(
        @AuthenticationPrincipal user: SecurityUserDetails
    ) : CommonResponse<SingleUserProfileResponse>
}

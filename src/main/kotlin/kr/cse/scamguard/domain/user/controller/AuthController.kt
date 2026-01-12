package kr.cse.scamguard.domain.user.controller

import io.swagger.v3.oas.annotations.Parameter
import jakarta.validation.Valid
import kr.cse.scamguard.common.model.CommonResponse
import kr.cse.scamguard.common.model.toSuccessResponse
import kr.cse.scamguard.common.security.SecurityUserDetails
import kr.cse.scamguard.domain.user.dto.*
import kr.cse.scamguard.domain.user.service.*
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
class AuthController (
    private val userService: UserService,
    private val userRegisterService: UserRegisterService,
    private val userLoginService: UserLoginService,
    private val userLogoutService: UserLogoutService,
    private val userLoginStatusService: UserLoginStatusService
) : AuthApi {

    @PostMapping("/api/v1/auth/join")
    override fun register(@Valid @RequestBody request: UserRegisterRequest): CommonResponse<UserRegisterResponse> =
        userRegisterService.execute(request.toCommand()).toSuccessResponse()

    @PostMapping("/api/v1/auth/login")
    override fun login(@Valid @RequestBody request: UserLoginRequest): CommonResponse<UserLoginResponse> =
        userLoginService.execute(request.toCommand()).toSuccessResponse()

    @PostMapping("/api/v1/auth/logout")
    @PreAuthorize("isAuthenticated()")
    override fun logout(
        @Parameter(hidden = true) @RequestHeader(value = "Authorization") authHeader: String,
        user: SecurityUserDetails
    ): CommonResponse<Nothing?> {
        return userLogoutService.execute(user.getUserId(), authHeader).toSuccessResponse()
    }

    @GetMapping("/api/v1/auth")
    @PreAuthorize("isAuthenticated()")
    override fun readAuthState(@Parameter(hidden = true) @RequestHeader(value = "Authorization") authHeader: String): CommonResponse<UserAuthStateResponse> =
        userLoginStatusService.readAuthStatus(authHeader).toSuccessResponse()

    @PostMapping("/api/v1/auth/refresh")
    override fun refresh(@RequestParam refreshToken: String): CommonResponse<UserAuthRefreshResponse> =
        userLoginStatusService.refresh(refreshToken).toSuccessResponse()

    @GetMapping("/api/v1/auth/profile")
    @PreAuthorize("isAuthenticated()")
    override fun getProfile(
        user: SecurityUserDetails
    ) : CommonResponse<SingleUserProfileResponse> =
        userService.getProfile(user.getUserId()).toSuccessResponse()
}

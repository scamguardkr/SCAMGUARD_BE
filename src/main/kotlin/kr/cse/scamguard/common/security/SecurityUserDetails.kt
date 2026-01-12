package kr.cse.scamguard.common.security

import com.fasterxml.jackson.annotation.JsonIgnore
import kr.cse.scamguard.common.exception.CustomException
import kr.cse.scamguard.domain.user.exception.UserErrorCode
import kr.cse.scamguard.domain.user.model.User
import kr.cse.scamguard.domain.user.model.UserType
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails


class SecurityUserDetails (
    private val userId: Long?,
    private val userName: String,
    @JsonIgnore
    private val password: String,
    private val roles: MutableSet<UserType>

) : UserDetails {

    companion object {
        fun from(user: User): SecurityUserDetails {
            return SecurityUserDetails(
                userId = user.id,
                userName = user.loginId,
                password = user.loginPw,
                roles = mutableSetOf(user.type)
            )
        }
    }

    fun getUserId(): Long {
        return userId ?: throw CustomException(UserErrorCode.NOT_FOUND_USER)
    }

    override fun getUsername(): String {
        return userName
    }

    override fun getPassword(): String {
        return password
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority>? {
        return roles.stream().map { role -> SimpleGrantedAuthority("ROLE_$role") }.toList()
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }
}

package kr.cse.scamguard.common.security

import kr.cse.scamguard.domain.user.repository.UserRepository
import kr.cse.scamguard.domain.user.repository.getByIdOrThrow
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service


@Service
class UserDetailsServiceImpl (
    private val userRepository: UserRepository,
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        return userRepository.getByIdOrThrow(username.toLong()).let { SecurityUserDetails.from(it) }
    }
}

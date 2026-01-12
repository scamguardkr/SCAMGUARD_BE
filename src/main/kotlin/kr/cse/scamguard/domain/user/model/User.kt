package kr.cse.scamguard.domain.user.model

import jakarta.persistence.*
import kr.cse.scamguard.common.exception.CustomException
import kr.cse.scamguard.common.model.BaseEntity
import kr.cse.scamguard.domain.user.exception.UserErrorCode
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class User(
    loginId: String,
    loginPw: String,
    name: String,
    email: String,
    type: UserType
): BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "login_id", unique = true, nullable = false, length = 50)
    var loginId: String = loginId
        protected set

    @Column(name = "login_pw", nullable = false)
    var loginPw: String = loginPw
        protected set

    @Column(name = "name", nullable = false, length = 100)
    var name: String = name
        protected set

    @Column(name = "email", unique = true, nullable = false, length = 100)
    var email: String = email
        protected set

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    var type: UserType = type
        protected set

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: UserStatus = UserStatus.ACTIVE
        protected set

    fun encodePassword(encodedPassword: String) {
        this.loginPw = encodedPassword
        updateTimestamp()
    }

    fun updatePassword(passwordEncoder: PasswordEncoder, newPassword: String) {
        val encodedPassword = passwordEncoder.encode(newPassword)
        encodePassword(encodedPassword)
    }

    fun validatePassword(passwordEncoder: PasswordEncoder, requestPw: String) {
        if(!passwordEncoder.matches(requestPw, this.loginPw)) {
            throw CustomException(UserErrorCode.INVALID_PASSWORD)
        }
    }

    fun activate() {
        this.status = UserStatus.ACTIVE
        updateTimestamp()
    }

    fun deactivate() {
        this.status = UserStatus.INACTIVE
        updateTimestamp()
    }

    fun suspend() {
        this.status = UserStatus.SUSPENDED
        updateTimestamp()
    }

    private fun updateTimestamp() {
        this.updatedAt = LocalDateTime.now()
    }
}

enum class UserStatus {
    ACTIVE, INACTIVE, SUSPENDED
}

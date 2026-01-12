package kr.cse.scamguard.common.security.jwt.config

import kr.cse.scamguard.common.security.jwt.filter.JwtAuthenticationFilter
import kr.cse.scamguard.common.security.jwt.filter.JwtExceptionFilter
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
class JwtSecurityAdapterConfig(
    private val daoAuthenticationProvider: DaoAuthenticationProvider,
    private val jwtExceptionFilter: JwtExceptionFilter,
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) : SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>() {

    override fun configure(httpSecurity: HttpSecurity) {
        httpSecurity.authenticationProvider(daoAuthenticationProvider)
        httpSecurity.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        httpSecurity.addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter::class.java)
    }
}

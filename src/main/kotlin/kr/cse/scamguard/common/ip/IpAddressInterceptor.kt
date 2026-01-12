package kr.cse.scamguard.common.ip

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class IpAddressInterceptor(
    private val ipAddressContext: IpAddressContext
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val clientIP = getClientIP(request)
        ipAddressContext.ipAddress = clientIP
        return true
    }

    private fun getClientIP(request: HttpServletRequest): String {
        var ip = request.getHeader("X-Forwarded-For")
        if (ip == null) {
            ip = request.getHeader("Proxy-Client-IP")
        }
        if (ip == null) {
            ip = request.getHeader("WL-Proxy-Client-IP")
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_CLIENT_IP")
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR")
        }
        if (ip == null) {
            ip = request.remoteAddr
        }
        return ip!!
    }
}

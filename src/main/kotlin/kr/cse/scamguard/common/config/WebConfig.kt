package kr.cse.scamguard.common.config

import kr.cse.scamguard.common.ip.IpAddressArgumentResolver
import kr.cse.scamguard.common.ip.IpAddressInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig (
    private val ipAddressInterceptor: IpAddressInterceptor,
    private val ipAddressArgumentResolver: IpAddressArgumentResolver
): WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(ipAddressInterceptor)
            .addPathPatterns("/**")
            .order(0)
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(ipAddressArgumentResolver)
    }
}

package kr.cse.scamguard.common.ip

import org.springframework.stereotype.Component
import org.springframework.web.context.annotation.RequestScope

@Component
@RequestScope
class IpAddressContext {
    var ipAddress: String? = null
}


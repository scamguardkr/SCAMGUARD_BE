package kr.cse.scamguard.common.ip

import io.swagger.v3.oas.annotations.Hidden

@Hidden
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class IpAddress()

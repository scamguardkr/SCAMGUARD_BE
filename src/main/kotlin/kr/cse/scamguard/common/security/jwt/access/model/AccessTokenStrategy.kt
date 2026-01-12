package kr.cse.scamguard.common.security.jwt.access.model

import org.springframework.beans.factory.annotation.Qualifier


@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION, AnnotationTarget.CLASS, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Qualifier("accessToken")
annotation class AccessTokenStrategy

package com.social.demo.presentation.config.security

import org.springframework.security.core.annotation.AuthenticationPrincipal

@Target
@Retention(AnnotationRetention.RUNTIME)
@AuthenticationPrincipal(expression = "memberId ?: \"\"")
annotation class LoginMember

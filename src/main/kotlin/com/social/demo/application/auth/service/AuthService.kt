package com.social.demo.application.auth.service

import com.social.demo.application.auth.port.`in`.AuthUseCase
import com.social.demo.application.auth.port.`in`.command.SocialClientCommand
import com.social.demo.application.auth.port.`in`.command.SocialLoginCommand
import org.springframework.stereotype.Service

@Service
class AuthService : AuthUseCase {
	override fun findAuthenticateSocialInfo(command: SocialClientCommand): String {
		return "findAuthenticateSocialInfo"
	}

	override fun socialLogin(command: SocialLoginCommand): String {
		return "socialLogin"
	}
}

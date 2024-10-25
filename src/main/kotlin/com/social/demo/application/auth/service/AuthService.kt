package com.social.demo.application.auth.service

import com.social.demo.application.auth.port.`in`.AuthUseCase
import com.social.demo.application.auth.port.`in`.command.SocialClientCommand
import com.social.demo.application.auth.port.`in`.command.SocialLoginCommand
import com.social.demo.external.model.SocialClientResponse
import com.social.demo.infrastructure.jpa.member.entity.OAuthProvider
import com.social.demo.presentation.dto.auth.response.AuthTokenResponse
import org.springframework.stereotype.Service

@Service
class AuthService : AuthUseCase {
	override fun findAuthenticateSocialInfo(command: SocialClientCommand): String {
		return "findAuthenticateSocialInfo"
	}

	override fun socialLogin(command: SocialLoginCommand): AuthTokenResponse {
		return AuthTokenResponse.of("accessToken", "refreshToken", false)
	}

	override fun authenticateFromProvider(
		oAuthProvider: OAuthProvider,
		token: String,
	): SocialClientResponse {
		return SocialClientResponse.of("oauthId", "email")
	}
}

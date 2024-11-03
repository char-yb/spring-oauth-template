package com.social.demo.application.auth.port.`in`

import com.social.demo.application.auth.port.`in`.command.SocialLoginCommand
import com.social.demo.external.model.SocialClientResponse
import com.social.demo.infrastructure.jpa.member.entity.OAuthProvider
import com.social.demo.presentation.dto.auth.response.AuthTokenResponse

interface AuthUseCase {
	fun socialLogin(command: SocialLoginCommand): AuthTokenResponse

	fun authenticateFromProvider(
		oAuthProvider: OAuthProvider,
		token: String,
	): SocialClientResponse
}

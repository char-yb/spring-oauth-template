package com.social.demo.presentation.api.auth

import com.social.demo.application.auth.port.`in`.AuthUseCase
import com.social.demo.application.auth.port.`in`.command.SocialLoginCommand
import com.social.demo.external.model.SocialClientResponse
import com.social.demo.infrastructure.jpa.member.entity.OAuthProvider
import com.social.demo.presentation.dto.auth.request.SocialLoginRequest
import com.social.demo.presentation.dto.auth.response.AuthTokenResponse
import io.swagger.v3.oas.annotations.Parameter
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
	private val authUseCase: AuthUseCase,
) : AuthApi {
	@RequestMapping("/social/{provider}")
	override fun socialLogin(
		@PathVariable(name = "provider") @Parameter(example = "apple", description = "OAuth 제공자") provider: String,
		@RequestBody request: @Valid SocialLoginRequest,
	): AuthTokenResponse {
		val oAuthProvider: OAuthProvider = OAuthProvider.from(provider)

		val socialClientResponse: SocialClientResponse =
			authUseCase.authenticateFromProvider(oAuthProvider, request.token)

		val socialLoginCommand: SocialLoginCommand =
			SocialLoginCommand.of(
				oAuthProvider,
				socialClientResponse.oauthId,
				socialClientResponse.email,
			)
		// 위 결과에서 나온 oauthId와 email로 토큰 발급
		return authUseCase.socialLogin(
			socialLoginCommand,
		)
	}
}

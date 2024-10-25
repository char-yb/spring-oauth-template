package com.social.demo.presentation.api.auth

import com.social.demo.presentation.dto.auth.request.SocialLoginRequest
import com.social.demo.presentation.dto.auth.response.AuthTokenResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "[인증]", description = "인증 관련 API입니다.")
interface AuthApi {
	@Operation(summary = "소셜 로그인", description = "소셜 로그인 후 임시 토큰을 발급합니다.")
	fun socialLogin(
		provider: String,
		request: SocialLoginRequest,
	): AuthTokenResponse
}

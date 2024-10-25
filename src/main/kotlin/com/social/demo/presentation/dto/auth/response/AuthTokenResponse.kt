package com.social.demo.presentation.dto.auth.response

import io.swagger.v3.oas.annotations.media.Schema

data class AuthTokenResponse(
	@Schema(description = "액세스 토큰", defaultValue = "accessToken")
	val accessToken: String,
	@Schema(description = "리프레시 토큰", defaultValue = "refreshToken")
	val refreshToken: String,
	@Schema(description = "임시 회원가입 여부", defaultValue = "true")
	val isTemporary: Boolean,
) {
	companion object {
		fun of(
			accessToken: String,
			refreshToken: String,
			isTemporary: Boolean,
		) = AuthTokenResponse(accessToken, refreshToken, isTemporary)
	}
}

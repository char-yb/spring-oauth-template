package com.social.demo.presentation.dto.auth.request

import io.swagger.v3.oas.annotations.media.Schema

data class SocialLoginRequest(
	@Schema(description = "apple auth code", example = "authorization_code")
	val token: String,
)

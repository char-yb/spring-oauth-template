package com.social.demo.external.model

data class SocialClientResponse(val email: String, val oauthId: String) {
	companion object {
		fun of(
			email: String,
			oauthId: String,
		) = SocialClientResponse(email, oauthId)
	}
}

package com.social.demo.domain.model.member.vo

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Embeddable

@Embeddable
data class OauthInfo(
	@Schema(
		description = "소셜 ID",
		example = "123487892",
	)
	var oauthId: String,
	@Schema(
		description = "소셜 제공자",
		example = "APPLE",
	)
	private val oauthProvider: String,
	@Schema(
		description = "소셜 이메일",
		example = "test@gmail.com",
	)
	private val oauthEmail: String,
) {
	companion object {
		fun createOauthInfo(
			oauthId: String,
			oauthProvider: String,
			oauthEmail: String,
		) = OauthInfo(
			oauthId = oauthId,
			oauthProvider = oauthProvider,
			oauthEmail = oauthEmail,
		)
	}
}

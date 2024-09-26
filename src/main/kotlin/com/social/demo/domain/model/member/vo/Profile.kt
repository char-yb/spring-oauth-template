package com.social.demo.domain.model.member.vo

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Embeddable

@Embeddable
data class Profile(
	@Schema(
		description = "닉네임",
		example = "왈왈멍",
	)
	private val nickname: String,
	@Schema(
		description = "프로필 이미지 URL",
		example = "./profile.jpg",
	)
	private val profileImageUrl: String,
) {
	companion object {
		fun createProfile(
			nickname: String,
			profileImageUrl: String,
		) = Profile(
			nickname = nickname,
			profileImageUrl = profileImageUrl,
		)
	}
}

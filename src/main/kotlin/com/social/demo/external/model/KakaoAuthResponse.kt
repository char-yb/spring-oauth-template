package com.social.demo.external.model

import com.fasterxml.jackson.annotation.JsonProperty

data class KakaoAuthResponse(
	val id: Long,
	@JsonProperty("kakao_account") val kakaoAccount: KakaoAccountResponse,
	@JsonProperty("properties") val properties: PropertiesResponse,
) {
	data class KakaoAccountResponse(val email: String)

	data class PropertiesResponse(
		val nickname: String,
		@JsonProperty("profile_image") val profileImage: String,
		@JsonProperty("thumbnail_image") val thumbnailImage: String,
	)
}

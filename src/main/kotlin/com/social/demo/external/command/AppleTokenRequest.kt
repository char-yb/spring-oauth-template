package com.social.demo.external.command

import com.fasterxml.jackson.annotation.JsonProperty

data class AppleTokenRequest(
	@JsonProperty("code")
	val code: String,
	@JsonProperty("client_id")
	val clientId: String,
	@JsonProperty("client_secret")
	val clientSecret: String,
	@JsonProperty("grant_type")
	val grantType: String,
) {
	companion object {
		fun of(
			code: String,
			clientId: String,
			clientSecret: String,
			grantType: String,
		): AppleTokenRequest {
			return AppleTokenRequest(code, clientId, clientSecret, grantType)
		}
	}
}

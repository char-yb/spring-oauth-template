package com.social.demo.external.model

data class AppleKeyResponse(
	val kty: String,
	val kid: String,
	val use: String,
	val alg: String,
	val n: String,
	val e: String,
)

package com.social.demo.common.constants

object SecurityConstants {
	const val TOKEN_ROLE_NAME: String = "role"
	const val TOKEN_PREFIX: String = "Bearer "
	const val APPLE_JWK_SET_URL: String = "https://appleid.apple.com/auth/keys"
	const val APPLE_ISSUER: String = "https://appleid.apple.com"
	const val APPLE_TOKEN_URL: String = "https://appleid.apple.com/auth/token"
	const val KAKAO_USER_ME_URL: String = "https://kapi.kakao.com/v2/user/me"
	const val APPLE_GRANT_TYPE: String = "authorization_code"
	const val ACCESS_TOKEN_COOKIE_NAME: String = "accessToken"
	const val REFRESH_TOKEN_COOKIE_NAME: String = "refreshToken"
	const val APPLICATION_URLENCODED: String = "application/x-www-form-urlencoded"
}

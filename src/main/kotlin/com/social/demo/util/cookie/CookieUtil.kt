package com.social.demo.util.cookie

import com.social.demo.common.constants.SecurityConstants.ACCESS_TOKEN_COOKIE_NAME
import com.social.demo.common.constants.SecurityConstants.REFRESH_TOKEN_COOKIE_NAME
import com.social.demo.util.env.SpringEnvironmentUtil
import org.springframework.boot.web.server.Cookie
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component

@Component
class CookieUtil(
	private val springEnvironmentUtil: SpringEnvironmentUtil,
) {
	fun generateTokenCookies(
		accessToken: String,
		refreshToken: String,
	): HttpHeaders {
		val sameSite = determineSameSitePolicy()

		val accessTokenCookie: ResponseCookie =
			ResponseCookie.from(ACCESS_TOKEN_COOKIE_NAME, accessToken)
				.path("/")
				.secure(true)
				.sameSite(sameSite)
				.httpOnly(true)
				.build()

		val refreshTokenCookie: ResponseCookie =
			ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
				.path("/")
				.secure(true)
				.sameSite(sameSite)
				.httpOnly(true)
				.build()

		val headers = HttpHeaders()
		headers.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
		headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())

		return headers
	}

	// https://arckwon.tistory.com/entry/%EB%B3%B4%EC%95%88-SameSite-Cookie-%EC%A0%81%EC%9A%A9-%EC%97%AC%EB%9F%AC%EA%B0%80%EC%A7%80%EB%B0%A9%EB%B2%95-GS%EC%9D%B8%EC%A6%9D-%EB%B3%B4%EC%95%88%EC%84%B1
	private fun determineSameSitePolicy(): String {
		if (springEnvironmentUtil.isProdProfile) {
			return Cookie.SameSite.STRICT.attributeValue()
		}
		return Cookie.SameSite.NONE.attributeValue()
	}
}

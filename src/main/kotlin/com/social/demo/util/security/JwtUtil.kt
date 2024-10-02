package com.social.demo.util.security

import com.social.demo.common.constants.SecurityConstants.TOKEN_ROLE_NAME
import com.social.demo.domain.model.auth.AccessTokenDto
import com.social.demo.domain.model.auth.RefreshTokenDto
import com.social.demo.domain.model.auth.TokenType
import com.social.demo.domain.model.member.Member
import com.social.demo.infrastructure.jpa.member.entity.MemberRole
import com.social.demo.infrastructure.jpa.member.entity.OAuthProvider
import com.social.demo.presentation.properties.JwtProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*

@Component
class JwtUtil(
	private val jwtProperties: JwtProperties,
) {
	fun generateAccessToken(
		memberId: String,
		memberRole: MemberRole,
	): String {
		val issuedAt = Date()
		val expiredAt =
			Date(issuedAt.time + jwtProperties.accessTokenExpirationMilliTime())
		return buildAccessToken(memberId, memberRole, issuedAt, expiredAt)
	}

	fun generateAccessTokenDto(
		memberId: String,
		memberRole: MemberRole,
	): AccessTokenDto {
		val issuedAt = Date()
		val expiredAt =
			Date(issuedAt.time + jwtProperties.accessTokenExpirationMilliTime())
		val tokenValue = buildAccessToken(memberId, memberRole, issuedAt, expiredAt)
		return AccessTokenDto(memberId, memberRole, tokenValue)
	}

	fun generateRefreshToken(memberId: String): String {
		val issuedAt = Date()
		val expiredAt =
			Date(issuedAt.time + jwtProperties.refreshTokenExpirationMilliTime())
		return buildRefreshToken(memberId, issuedAt, expiredAt)
	}

	fun generateRefreshTokenDto(memberId: String): RefreshTokenDto {
		val issuedAt = Date()
		val expiredAt =
			Date(issuedAt.time + jwtProperties.refreshTokenExpirationMilliTime())
		val tokenValue = buildRefreshToken(memberId, issuedAt, expiredAt)
		return RefreshTokenDto(
			memberId,
			tokenValue,
			jwtProperties.refreshTokenExpirationTime,
		)
	}

	@Throws(ExpiredJwtException::class)
	fun parseAccessToken(token: String): AccessTokenDto? {
		// 토큰 파싱하여 성공하면 AccessTokenDto 반환, 실패하면 null 반환
		// 만료된 토큰인 경우에만 ExpiredJwtException 발생
		try {
			val claims: Jws<Claims> = getClaims(token, accessTokenKey)

			return AccessTokenDto(
				claims.body.subject.toString(),
				MemberRole.valueOf(claims.body.get(TOKEN_ROLE_NAME, String::class.java)),
				token,
			)
		} catch (e: ExpiredJwtException) {
			throw e
		} catch (e: Exception) {
			return null
		}
	}

	@Throws(ExpiredJwtException::class)
	fun parseRefreshToken(token: String): RefreshTokenDto? {
		try {
			val claims: Jws<Claims> = getClaims(token, refreshTokenKey)

			return RefreshTokenDto(
				claims.body.subject.toString(),
				token,
				jwtProperties.refreshTokenExpirationTime,
			)
		} catch (e: ExpiredJwtException) {
			throw e
		} catch (e: Exception) {
			return null
		}
	}

	val refreshTokenExpirationTime: Long
		get() = jwtProperties.refreshTokenExpirationTime

	private val refreshTokenKey: Key
		get() = Keys.hmacShaKeyFor(jwtProperties.refreshTokenSecret.toByteArray())

	private val accessTokenKey: Key
		get() = Keys.hmacShaKeyFor(jwtProperties.accessTokenSecret.toByteArray())
	// 	toByteArray(Charset.defaultCharset())

	private fun getClaims(
		token: String,
		key: Key,
	): Jws<Claims> {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
	}

	private fun generateTemporaryTokenExpiration(): Date {
		return Date(Long.MAX_VALUE)
	}

	fun generateTemporaryToken(
		oAuthProvider: OAuthProvider,
		member: Member,
	): String {
		return Jwts.builder()
			.setHeader(createTokenHeader(TokenType.TEMPORARY))
			.setSubject(member.memberId)
			.setClaims(
				mapOf(
					USER_ID_KEY_NAME to member.oauthInfo.oauthId,
					PROVIDER_KEY_NAME to oAuthProvider.value,
					TOKEN_ROLE_NAME to MemberRole.TEMPORARY.name,
				),
			)
			.setExpiration(generateTemporaryTokenExpiration())
			.signWith(accessTokenKey)
			.compact()
	}

	private fun buildAccessToken(
		memberId: String,
		memberRole: MemberRole,
		issuedAt: Date,
		expiredAt: Date,
	): String {
		return Jwts.builder()
			.setHeader(
				createTokenHeader(
					if (memberRole === MemberRole.TEMPORARY
					) {
						TokenType.TEMPORARY
					} else {
						TokenType.ACCESS
					},
				),
			)
			.setSubject(memberId.toString())
			.claim(TOKEN_ROLE_NAME, memberRole.name)
			.setIssuedAt(issuedAt)
			.setExpiration(expiredAt)
			.signWith(accessTokenKey)
			.compact()
	}

	private fun buildRefreshToken(
		memberId: String,
		issuedAt: Date,
		expiredAt: Date,
	): String {
		return Jwts.builder()
			.setHeader(createTokenHeader(TokenType.REFRESH))
			.setSubject(memberId)
			.setIssuedAt(issuedAt)
			.setExpiration(expiredAt)
			.signWith(refreshTokenKey)
			.compact()
	}

	private fun createTokenHeader(tokenType: TokenType): Map<String, Any> {
		return mapOf(
			"typ" to "JWT" as Any,
			"alg" to "HS256" as Any,
			"regDate" to System.currentTimeMillis() as Any,
			TOKEN_TYPE_KEY_NAME to tokenType.value as Any,
		)
	}

	companion object {
		private const val TOKEN_TYPE_KEY_NAME = "type"
		private const val USER_ID_KEY_NAME = "memberId"
		private const val PROVIDER_KEY_NAME = "provider"
	}
}

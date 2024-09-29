package com.social.demo.application.auth.service

import com.social.demo.application.auth.port.`in`.TokenProvider
import com.social.demo.common.constants.SecurityConstants.TOKEN_ROLE_NAME
import com.social.demo.domain.model.auth.AccessTokenDto
import com.social.demo.domain.model.auth.RefreshTokenDto
import com.social.demo.domain.model.member.Member
import com.social.demo.infrastructure.jpa.auth.repository.RefreshTokenRepository
import com.social.demo.infrastructure.jpa.member.entity.MemberRole
import com.social.demo.infrastructure.redis.RefreshToken
import com.social.demo.presentation.dto.auth.response.TokenPairResponse
import com.social.demo.util.security.JwtUtil
import io.jsonwebtoken.ExpiredJwtException
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtTokenProvider(
	private val jwtUtil: JwtUtil,
	private val refreshTokenRepository: RefreshTokenRepository,
) : TokenProvider {
	override fun generateTokenPair(
		memberId: String,
		memberRole: MemberRole,
	): TokenPairResponse {
		val accessToken = createAccessToken(memberId, memberRole)
		val refreshToken = createRefreshToken(memberId)
		return TokenPairResponse.of(accessToken, refreshToken)
	}

	override fun generateTemporaryTokenPair(temporaryMember: Member): TokenPairResponse {
		val memberId = temporaryMember.id
		val accessToken = createAccessToken(memberId, MemberRole.TEMPORARY)
		val refreshToken = createRefreshToken(memberId)
		return TokenPairResponse.of(accessToken, refreshToken)
	}

	private fun createAccessToken(
		memberId: String,
		memberRole: MemberRole,
	): String {
		return jwtUtil.generateAccessToken(memberId, memberRole)
	}

	override fun createAccessTokenDto(
		memberId: String,
		memberRole: MemberRole,
	): AccessTokenDto {
		return jwtUtil.generateAccessTokenDto(memberId, memberRole)
	}

	private fun createRefreshToken(memberId: String): String {
		val token: String = jwtUtil.generateRefreshToken(memberId)
		saveRefreshTokenToRedis(memberId, token, jwtUtil.refreshTokenExpirationTime)
		return token
	}

	override fun createRefreshTokenDto(memberId: String): RefreshTokenDto {
		val refreshTokenDto: RefreshTokenDto = jwtUtil.generateRefreshTokenDto(memberId)
		saveRefreshTokenToRedis(memberId, refreshTokenDto.tokenValue, refreshTokenDto.ttl)
		return refreshTokenDto
	}

	private fun saveRefreshTokenToRedis(
		memberId: String,
		refreshTokenDto: String,
		ttl: Long,
	) {
		val refreshToken = RefreshToken(memberId, refreshTokenDto, ttl)
		refreshTokenRepository.save(refreshToken)
	}

	override fun retrieveAccessToken(accessTokenValue: String): AccessTokenDto? {
		return try {
			jwtUtil.parseAccessToken(accessTokenValue)
		} catch (e: Exception) {
			null
		}
	}

	override fun retrieveRefreshToken(refreshTokenValue: String): RefreshTokenDto? {
		val refreshTokenDto: RefreshTokenDto? = parseRefreshToken(refreshTokenValue)
		println("refreshTokenDto: $refreshTokenDto")
		if (refreshTokenDto == null) {
			return null
		}

		// 파싱된 DTO와 일치하는 토큰이 Redis에 저장되어 있는지 확인
		val refreshToken: Optional<RefreshToken?> = getRefreshTokenFromRedis(refreshTokenDto.memberId)

		// Redis에 토큰이 존재하고, 쿠키의 토큰과 값이 일치하면 DTO 반환
		if (refreshToken.isPresent) {
			return refreshTokenDto
		}

		// Redis에 토큰이 존재하지 않거나, 쿠키의 토큰과 값이 일치하지 않으면 null 반환
		return null
	}

	private fun getRefreshTokenFromRedis(memberId: String): Optional<RefreshToken?> {
		return refreshTokenRepository.findById(memberId)
	}

	private fun parseRefreshToken(refreshTokenValue: String): RefreshTokenDto? {
		return try {
			jwtUtil.parseRefreshToken(refreshTokenValue)
		} catch (e: Exception) {
			null
		}
	}

	override fun reissueAccessTokenIfExpired(accessTokenValue: String): AccessTokenDto? {
		// AT가 만료된 경우 AT 재발급, 만료되지 않은 경우 null 반환
		try {
			jwtUtil.parseAccessToken(accessTokenValue)
			return null
		} catch (e: ExpiredJwtException) {
			val memberId: String = e.claims.subject.toString()
			val memberRole: MemberRole =
				MemberRole.valueOf(e.claims.get(TOKEN_ROLE_NAME, String::class.java))
			return createAccessTokenDto(memberId, memberRole)
		}
	}
}

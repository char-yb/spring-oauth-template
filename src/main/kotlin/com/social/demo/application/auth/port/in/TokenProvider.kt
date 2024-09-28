package com.social.demo.application.auth.port.`in`

import com.social.demo.domain.model.auth.AccessTokenDto
import com.social.demo.domain.model.auth.RefreshTokenDto
import com.social.demo.domain.model.member.Member
import com.social.demo.infrastructure.jpa.member.entity.MemberRole
import com.social.demo.presentation.dto.auth.response.TokenPairResponse

interface TokenProvider {
	fun generateTokenPair(
		memberId: String,
		memberRole: MemberRole,
	): TokenPairResponse

	fun generateTemporaryTokenPair(temporaryMember: Member): TokenPairResponse

	fun createAccessTokenDto(
		memberId: String,
		memberRole: MemberRole,
	): AccessTokenDto

	fun createRefreshTokenDto(memberId: String): RefreshTokenDto

	fun retrieveAccessToken(accessTokenValue: String): AccessTokenDto?

	fun retrieveRefreshToken(refreshTokenValue: String): RefreshTokenDto?

	fun reissueAccessTokenIfExpired(accessTokenValue: String): AccessTokenDto?
}

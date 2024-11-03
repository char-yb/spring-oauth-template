package com.social.demo.application.auth.service

import com.social.demo.application.auth.port.`in`.AuthUseCase
import com.social.demo.application.auth.port.`in`.command.SocialLoginCommand
import com.social.demo.domain.gateway.member.MemberGateway
import com.social.demo.domain.model.member.Member
import com.social.demo.external.client.AppleClient
import com.social.demo.external.client.KakaoClient
import com.social.demo.external.model.SocialClientResponse
import com.social.demo.infrastructure.jpa.member.entity.MemberRole
import com.social.demo.infrastructure.jpa.member.entity.OAuthProvider
import com.social.demo.infrastructure.jpa.member.entity.OAuthProvider.*
import com.social.demo.presentation.dto.auth.response.AuthTokenResponse
import com.social.demo.presentation.dto.auth.response.TokenPairResponse
import com.social.demo.util.logging.logger
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthService(
	private val appleClient: AppleClient,
	private val kakaoClient: KakaoClient,
	private val memberGateway: MemberGateway,
	private val jwtTokenProvider: JwtTokenProvider,
) : AuthUseCase {
	private val log by logger()

	override fun socialLogin(command: SocialLoginCommand): AuthTokenResponse {
		val memberOptional: Member? =
			memberGateway.findByOauthInfoOauthProviderAndOauthInfoOauthId(
				command.oAuthProvider.value,
				command.oauthId,
			)

		return memberOptional
			?.let { member: Member ->
				// 사용자 로그인 토큰 생성
				val tokenPair: TokenPairResponse =
					if (member.role == MemberRole.TEMPORARY) {
						jwtTokenProvider.generateTokenPair(member.memberId, MemberRole.TEMPORARY)
					} else {
						jwtTokenProvider.generateTokenPair(member.memberId, MemberRole.USER)
					}
// 					member.updateLastLoginAt()
// 					updateMemberNormalStatus(member)
				log.info("소셜 로그인 진행: {}", member.memberId)
				AuthTokenResponse.of(
					tokenPair.accessToken, tokenPair.refreshToken, member.role == MemberRole.TEMPORARY,
				)
			} ?: run {
			// 회원가입이 안된 경우, 임시 회원가입 진행
			val newMember: Member =
				Member.createOAuthMember(command.oAuthProvider, command.oauthId, command.email)
			memberGateway.save(newMember)

			// 임시 토큰 발행
			val temporaryTokenPair: TokenPairResponse =
				jwtTokenProvider.generateTemporaryTokenPair(newMember)
// 				newMember.updateLastLoginAt()
			log.info("임시 회원가입 진행: {}", newMember.memberId)
			AuthTokenResponse.of(temporaryTokenPair.accessToken, temporaryTokenPair.refreshToken, true)
		}
	}

	override fun authenticateFromProvider(
		provider: OAuthProvider,
		token: String,
	): SocialClientResponse {
		/* token
        1. apple의 경우 authorizationCode Value
        2. kakao의 경우 accessToken Value
		 */
		return when (provider) {
			APPLE -> appleClient.authenticateFromApple(token)
			KAKAO -> kakaoClient.authenticateFromKakao(token)
		}
	}
}

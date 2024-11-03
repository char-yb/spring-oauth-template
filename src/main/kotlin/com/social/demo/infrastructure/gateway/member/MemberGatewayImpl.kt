package com.social.demo.infrastructure.gateway.member

import com.social.demo.common.annotation.Gateway
import com.social.demo.domain.gateway.member.MemberGateway
import com.social.demo.domain.model.member.Member
import com.social.demo.infrastructure.jpa.member.repository.MemberRepository
import com.social.demo.presentation.exception.CustomException
import com.social.demo.presentation.exception.ErrorCode

@Gateway
class MemberGatewayImpl(
	private val memberRepository: MemberRepository,
) : MemberGateway {
	override fun findById(id: String): Member {
		return memberRepository.findById(id).orElseThrow { CustomException(ErrorCode.MEMBER_NOT_FOUND) }.toDomain()
	}

	override fun findByOauthInfoOauthProviderAndOauthInfoOauthId(
		provider: String,
		oauthId: String,
	): Member? {
		return memberRepository.findByOauthInfoOauthProviderAndOauthInfoOauthId(provider, oauthId)?.toDomain()
	}

	override fun save(member: Member): Member {
		return memberRepository.save(member.toEntity()).toDomain()
	}
}

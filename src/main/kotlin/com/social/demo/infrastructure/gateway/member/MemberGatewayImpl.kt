package com.social.demo.infrastructure.gateway.member

import com.social.demo.common.annotation.Gateway
import com.social.demo.domain.gateway.member.MemberGateway
import com.social.demo.infrastructure.jpa.member.repository.MemberRepository

@Gateway
class MemberGatewayImpl(
	private val memberRepository: MemberRepository,
) : MemberGateway

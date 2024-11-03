package com.social.demo.domain.gateway.member

import com.social.demo.domain.model.member.Member

interface MemberGateway {
	fun findById(id: String): Member

	fun findByOauthInfoOauthProviderAndOauthInfoOauthId(
		provider: String,
		oauthId: String,
	): Member?

	fun save(member: Member): Member
}

package com.social.demo.domain.model.member

import com.social.demo.domain.model.member.vo.OauthInfo
import com.social.demo.domain.model.member.vo.Profile
import com.social.demo.infrastructure.jpa.member.entity.MemberRole

data class Member(
	val id: String,
	val nickname: String,
	val oauthInfo: OauthInfo,
	val profile: Profile,
	val role: MemberRole = MemberRole.USER,
) {
	companion object {
		fun createMember(
			id: String,
			nickname: String,
			oauthInfo: OauthInfo,
			profile: Profile,
			role: MemberRole,
		) = Member(
			id,
			nickname,
			oauthInfo,
			profile,
			role,
		)
	}
}

package com.social.demo.domain.model.member

import com.social.demo.domain.model.member.vo.OauthInfo
import com.social.demo.domain.model.member.vo.Profile
import com.social.demo.infrastructure.jpa.member.entity.MemberRole
import com.social.demo.util.ulid.generateULID

data class Member(
	val memberId: String,
	val nickname: String,
	val oauthInfo: OauthInfo,
	val profile: Profile,
	val role: MemberRole = MemberRole.USER,
) {
	companion object {
		fun createMember(
			nickname: String,
			oauthInfo: OauthInfo,
			profile: Profile,
			role: MemberRole,
		) = Member(
			memberId = generateULID(),
			nickname,
			oauthInfo,
			profile,
			role,
		)
	}
}

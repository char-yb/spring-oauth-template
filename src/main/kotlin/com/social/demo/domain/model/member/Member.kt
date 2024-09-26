package com.social.demo.domain.model.member

import com.social.demo.domain.model.member.vo.OauthInfo
import com.social.demo.domain.model.member.vo.Profile

data class Member(
	val id: String,
	val nickname: String,
	val oauthInfo: OauthInfo,
	val profile: Profile,
) {
	companion object {
		fun createMember(
			id: String,
			nickname: String,
			oauthInfo: OauthInfo,
			profile: Profile,
		) = Member(
			id = id,
			nickname = nickname,
			oauthInfo = oauthInfo,
			profile = profile,
		)
	}
}

package com.social.demo.domain.model.member

import com.social.demo.domain.model.member.vo.OauthInfo
import com.social.demo.domain.model.member.vo.Profile
import com.social.demo.infrastructure.jpa.member.entity.MemberEntity
import com.social.demo.infrastructure.jpa.member.entity.MemberRole
import com.social.demo.infrastructure.jpa.member.entity.OAuthProvider
import com.social.demo.util.ulid.generateULID

data class Member(
	val memberId: String,
	val nickname: String,
	val oauthInfo: OauthInfo,
	val profile: Profile,
	val role: MemberRole = MemberRole.USER,
) {
	fun toEntity() = MemberEntity.fromDomain(this)

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

		fun createOAuthMember(
			oAuthProvider: OAuthProvider,
			oauthId: String,
			email: String,
		): Member {
			val oauthInfo = OauthInfo(oAuthProvider.value, oauthId, email)
			val profile = Profile.createProfile("", "")
			return createMember(email, oauthInfo, profile, MemberRole.TEMPORARY)
		}
	}
}

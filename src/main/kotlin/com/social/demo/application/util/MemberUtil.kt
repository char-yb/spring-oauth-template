package com.social.demo.application.util
import com.social.demo.domain.gateway.member.MemberGateway
import com.social.demo.domain.model.member.Member
import com.social.demo.util.security.SecurityUtil
import org.springframework.stereotype.Component

@Component
class MemberUtil(
	private val securityUtil: SecurityUtil,
	private val memberGateway: MemberGateway,
) {
	val currentMember: Member
		get() =
			memberGateway
				.findById(securityUtil.currentMemberId)

	fun getMemberByMemberId(memberId: String) =
		memberGateway
			.findById(memberId)

	val memberRole: String
		get() = securityUtil.currentMemberRole
}

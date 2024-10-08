package com.social.demo.infrastructure.jpa.member.entity

import com.social.demo.domain.model.member.Member
import com.social.demo.domain.model.member.vo.OauthInfo
import com.social.demo.domain.model.member.vo.Profile
import com.social.demo.infrastructure.jpa.BaseEntity
import jakarta.persistence.*
import kotlin.reflect.full.isSubclassOf

@Entity(name = "member")
class MemberEntity private constructor(
	@Id
	@Column(name = "member_id", length = 26, columnDefinition = "CHAR(26)", nullable = false)
	val memberId: String,
	@Column(name = "name", nullable = false)
	val nickname: String,
	val oauthInfo: OauthInfo,
	val profile: Profile,
	val role: MemberRole = MemberRole.USER,
) : BaseEntity() {
	fun toDomain() =
		Member(
			memberId = memberId,
			nickname = nickname,
			oauthInfo = oauthInfo,
			profile = profile,
			role = role,
		)

	// Proxy 객체 고려하여 equals Override, https://zins.tistory.com/19
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is MemberEntity) return false
		if (!compareClassesIncludeProxy(other)) return false
		if (memberId != other.memberId) return false
		return true
	}

	private fun compareClassesIncludeProxy(other: Any) =
		this::class.isSubclassOf(other::class) ||
			other::class.isSubclassOf(this::class)

	override fun hashCode(): Int = memberId.hashCode()

	companion object {
		fun fromDomain(member: Member) =
			with(member) {
				MemberEntity(
					memberId = memberId,
					nickname = nickname,
					oauthInfo = oauthInfo,
					profile = profile,
					role = role,
				)
			}
	}
}

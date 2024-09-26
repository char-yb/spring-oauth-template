package com.social.demo.infrastructure.jpa.member.entity

import com.social.demo.domain.model.member.Member
import com.social.demo.domain.model.member.vo.OauthInfo
import com.social.demo.domain.model.member.vo.Profile
import jakarta.persistence.*
import kotlin.reflect.full.isSubclassOf

@Entity(name = "member")
class MemberEntity private constructor(
	@Id
	@Column(name = "id", length = 26, columnDefinition = "CHAR(26)", nullable = false)
	val id: String,
	@Column(name = "name", nullable = false)
	val nickname: String,
	val oauthInfo: OauthInfo,
	val profile: Profile,
) {
	fun toDomain() =
		Member(
			id = id,
			nickname = nickname,
			oauthInfo = oauthInfo,
			profile = profile,
		)

	// Proxy 객체 고려하여 equals Override, https://zins.tistory.com/19
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is MemberEntity) return false
		if (!compareClassesIncludeProxy(other)) return false
		if (id != other.id) return false
		return true
	}

	private fun compareClassesIncludeProxy(other: Any) =
		this::class.isSubclassOf(other::class) ||
			other::class.isSubclassOf(this::class)

	override fun hashCode(): Int = id.hashCode() ?: 0

	companion object {
		fun fromDomain(member: Member) =
			with(member) {
				MemberEntity(
					id = id,
					nickname = nickname,
					oauthInfo = oauthInfo,
					profile = profile,
				)
			}
	}
}

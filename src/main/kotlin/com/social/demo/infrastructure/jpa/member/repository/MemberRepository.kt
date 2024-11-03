package com.social.demo.infrastructure.jpa.member.repository

import com.social.demo.infrastructure.jpa.member.entity.MemberEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<MemberEntity, String> {
	fun findByOauthInfoOauthProviderAndOauthInfoOauthId(
		provider: String,
		oauthId: String,
	): MemberEntity?
}

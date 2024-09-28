package com.social.demo.infrastructure.jpa.member.entity

enum class MemberRole(val value: String?) {
	USER("ROLE_USER"),
	ADMIN("ROLE_ADMIN"),
	TEMPORARY("ROLE_TEMPORARY"),
}

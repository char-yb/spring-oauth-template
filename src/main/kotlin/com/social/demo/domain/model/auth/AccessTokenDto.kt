package com.social.demo.domain.model.auth

import com.social.demo.infrastructure.jpa.member.entity.MemberRole

data class AccessTokenDto(val memberId: String, val memberRole: MemberRole, val tokenValue: String)

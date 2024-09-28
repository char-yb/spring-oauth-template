package com.social.demo.domain.model.auth

data class RefreshTokenDto(val memberId: Long, val tokenValue: String, val ttl: Long)

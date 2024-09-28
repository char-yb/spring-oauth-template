package com.social.demo.domain.model.auth

data class RefreshTokenDto(val memberId: String, val tokenValue: String, val ttl: Long)

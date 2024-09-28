package com.social.demo.infrastructure.redis

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive

@RedisHash(value = "refreshToken")
class RefreshToken(
	@Id private val memberId: String,
	private val token: String,
	@TimeToLive private val ttl: Long,
)

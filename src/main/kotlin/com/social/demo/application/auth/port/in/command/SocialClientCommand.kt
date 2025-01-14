package com.social.demo.application.auth.port.`in`.command

import com.social.demo.infrastructure.jpa.member.entity.OAuthProvider

data class SocialClientCommand(
	val oAuthProvider: OAuthProvider,
	val authToken: String,
)

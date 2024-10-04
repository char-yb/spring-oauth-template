package com.social.demo.application.auth.port.`in`.command

import com.social.demo.infrastructure.jpa.member.entity.OAuthProvider

data class SocialLoginCommand(
	val oAuthProvider: OAuthProvider,
	val oauthId: String,
	val email: String,
)

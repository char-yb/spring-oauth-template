package com.social.demo.application.auth.port.`in`

import com.social.demo.application.auth.port.`in`.command.SocialClientCommand
import com.social.demo.application.auth.port.`in`.command.SocialLoginCommand

interface AuthUseCase {
	fun getAuthenticateSocialInfo(command: SocialClientCommand): String

	fun socialLogin(command: SocialLoginCommand): String
}

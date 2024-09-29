package com.social.demo.util.env

import com.social.demo.common.constants.EnvironmentConstants
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import java.util.*
import java.util.stream.Stream

@Component
class SpringEnvironmentUtil(
	private val environment: Environment,
) {
	val currentProfile: String
		get() =
			activeProfiles
				.filter {
						profile: String ->
					profile == EnvironmentConstants.PROD.value || profile == EnvironmentConstants.DEV.value
				}
				.findFirst()
				.orElse(EnvironmentConstants.LOCAL.value)

	val isProdProfile: Boolean
		get() = activeProfiles.anyMatch(EnvironmentConstants.PROD.value::equals)

	val isDevProfile: Boolean
		get() = activeProfiles.anyMatch(EnvironmentConstants.DEV.value::equals)

	val isProdAndDevProfile: Boolean
		get() = activeProfiles.anyMatch(EnvironmentConstants.PROD_AND_DEV_ENV::contains)

	private val activeProfiles: Stream<String>
		get() = Arrays.stream(environment.activeProfiles)
}

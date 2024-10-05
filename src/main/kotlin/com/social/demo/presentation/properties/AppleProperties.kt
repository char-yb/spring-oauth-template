package com.social.demo.presentation.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "apple")
data class AppleProperties(
	val dev: EnvironmentProperties,
	val prod: EnvironmentProperties,
	val keyId: String,
	val p8: String,
) {
	data class EnvironmentProperties(val clientId: String, val teamId: String)
}

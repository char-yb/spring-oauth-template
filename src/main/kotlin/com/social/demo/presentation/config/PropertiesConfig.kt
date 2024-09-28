package com.social.demo.presentation.config

import com.social.demo.presentation.properties.JwtProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@EnableConfigurationProperties(
	JwtProperties::class,
)
@Configuration
class PropertiesConfig

package com.social.demo.presentation.config.properties

import com.social.demo.presentation.properties.JwtProperties
import com.social.demo.presentation.properties.RedisProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@EnableConfigurationProperties(
	JwtProperties::class,
	RedisProperties::class,
)
@Configuration
class PropertiesConfig

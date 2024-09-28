package com.social.demo.domain.model.auth

import java.security.InvalidParameterException
import java.util.*

enum class TokenType(val value: String?) {
	ACCESS("access"),
	REFRESH("refresh"),
	TEMPORARY("temporary"),
	;

	companion object {
		fun from(typeKey: String): TokenType {
			return when (typeKey.uppercase(Locale.getDefault())) {
				"ACCESS" -> ACCESS
				"REFRESH" -> REFRESH
				"TEMPORARY" -> TEMPORARY
				else -> throw InvalidParameterException()
			}
		}
	}
}

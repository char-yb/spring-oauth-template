package com.social.demo.common.constants

enum class EnvironmentConstants(val value: String) {
	PROD("prod"),
	DEV("dev"),
	LOCAL("local"),
	;

	companion object {
		val PROD_AND_DEV_ENV = listOf(PROD.value, DEV.value)
	}
}

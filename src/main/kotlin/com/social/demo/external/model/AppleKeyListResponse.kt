package com.social.demo.external.model

import java.util.*

data class AppleKeyListResponse(val keys: Array<AppleKeyResponse>) {
	override fun equals(other: Any?): Boolean {
		return (
			other is AppleKeyListResponse &&
				other.keys.contentEquals(keys)
		)
	}

	override fun hashCode(): Int {
		return keys.contentHashCode()
	}

	override fun toString(): String {
		return "AppleKeyListResponse{" + "keys=" + keys.contentToString() + '}'
	}

	fun getMatchedKeyBy(
		kid: String,
		alg: String,
	): Optional<AppleKeyResponse> {
		return Arrays.stream(keys)
			.filter { key: AppleKeyResponse -> key.kid == kid && key.alg == alg }
			.findFirst()
	}
}

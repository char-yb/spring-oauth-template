package com.social.demo.util.security
import com.social.demo.presentation.exception.CustomException
import com.social.demo.presentation.exception.ErrorCode
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class SecurityUtil {
	val currentMemberId: String
		get() {
			val authentication: Authentication = SecurityContextHolder.getContext().authentication
			try {
				return authentication.name
			} catch (e: Exception) {
				throw CustomException(ErrorCode.AUTH_NOT_FOUND)
			}
		}

	val currentMemberRole: String
		get() {
			val authentication: Authentication = SecurityContextHolder.getContext().authentication
			try {
				return authentication.authorities.stream().findFirst().get().authority
			} catch (e: Exception) {
				throw CustomException(ErrorCode.AUTH_NOT_FOUND)
			}
		}
}

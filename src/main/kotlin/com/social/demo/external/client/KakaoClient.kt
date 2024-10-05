package com.social.demo.external.client

import com.social.demo.common.annotation.ExternalClient
import com.social.demo.common.constants.SecurityConstants.KAKAO_USER_ME_URL
import com.social.demo.common.constants.SecurityConstants.TOKEN_PREFIX
import com.social.demo.external.model.KakaoAuthResponse
import com.social.demo.external.model.SocialClientResponse
import com.social.demo.presentation.exception.CustomException
import com.social.demo.presentation.exception.ErrorCode
import org.springframework.http.HttpRequest
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse
import java.util.*

@ExternalClient
class KakaoClient(
	private val restClient: RestClient,
) {
	fun authenticateFromKakao(token: String): SocialClientResponse {
		val kakaoAuthResponse =
			restClient
				.get()
				.uri(KAKAO_USER_ME_URL)
				.header("Authorization", TOKEN_PREFIX + token)
				.exchange<KakaoAuthResponse>
				{ _: HttpRequest?, response: ConvertibleClientHttpResponse ->
					if (!response.statusCode.is2xxSuccessful) {
						throw CustomException(
							ErrorCode.KAKAO_TOKEN_CLIENT_FAILED,
						)
					}
					Objects.requireNonNull(
						response.bodyTo(KakaoAuthResponse::class.java),
					)!!
				}

		return SocialClientResponse(
			kakaoAuthResponse.kakaoAccount.email,
			kakaoAuthResponse.id.toString(),
		)
	}
}

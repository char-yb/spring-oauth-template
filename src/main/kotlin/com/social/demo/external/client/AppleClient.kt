package com.social.demo.external.client

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.nimbusds.jose.JOSEException
import com.nimbusds.jose.jwk.JWK
import com.social.demo.common.annotation.ExternalClient
import com.social.demo.common.constants.SecurityConstants.APPLE_GRANT_TYPE
import com.social.demo.common.constants.SecurityConstants.APPLE_ISSUER
import com.social.demo.common.constants.SecurityConstants.APPLE_JWK_SET_URL
import com.social.demo.common.constants.SecurityConstants.APPLE_TOKEN_URL
import com.social.demo.common.constants.SecurityConstants.APPLICATION_URLENCODED
import com.social.demo.external.command.AppleTokenRequest
import com.social.demo.external.model.AppleKeyListResponse
import com.social.demo.external.model.AppleKeyResponse
import com.social.demo.external.model.AppleTokenResponse
import com.social.demo.external.model.SocialClientResponse
import com.social.demo.presentation.exception.CustomException
import com.social.demo.presentation.exception.ErrorCode
import com.social.demo.presentation.properties.AppleProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import org.springframework.http.HttpHeaders
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestClient
import java.security.InvalidParameterException
import java.security.Key
import java.security.PrivateKey
import java.security.Security
import java.text.ParseException
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@ExternalClient
class AppleClient(
	private val objectMapper: ObjectMapper,
	private val restClient: RestClient,
	private val appleProperties: AppleProperties,
) {
	companion object {
		private const val APPLE_TOKEN_EXPIRE_MINUTES = 5
	}

	// apple server에서 받아온 id_token
	private fun getAppleToken(appleTokenRequest: AppleTokenRequest): AppleTokenResponse {
		// Prepare form data
		val formData: MultiValueMap<String, String> = LinkedMultiValueMap()
		formData.add("client_id", appleTokenRequest.clientId)
		formData.add("client_secret", appleTokenRequest.clientSecret)
		formData.add("code", appleTokenRequest.code)
		formData.add("grant_type", appleTokenRequest.grantType)

		val responseEntity =
			restClient
				.post()
				.uri(APPLE_TOKEN_URL)
				.header(HttpHeaders.CONTENT_TYPE, APPLICATION_URLENCODED)
				.body(formData)
				.retrieve()
				.toEntity(AppleTokenResponse::class.java)

		if (!responseEntity.statusCode.is2xxSuccessful) {
			throw CustomException(ErrorCode.APPLE_TOKEN_CLIENT_FAILED)
		}

		return responseEntity.body ?: throw CustomException(ErrorCode.APPLE_TOKEN_CLIENT_FAILED)
	}

	private fun generateAppleClientSecret(): String {
		val expirationDate =
			Date.from(
				LocalDateTime.now()
					.plusMinutes(APPLE_TOKEN_EXPIRE_MINUTES.toLong())
					.atZone(ZoneId.systemDefault())
					.toInstant(),
			)

		return Jwts.builder()
			.setHeaderParam("kid", appleProperties.keyId)
			.setHeaderParam("alg", "ES256") // TODO: dev, prod 환경분리 필요
			.setIssuer(appleProperties.dev.teamId.split('.')[0])
			.setIssuedAt(Date(System.currentTimeMillis()))
			.setExpiration(expirationDate)
			.setAudience(APPLE_ISSUER)
			.setSubject(appleProperties.dev.clientId)
			.signWith(privateKey, SignatureAlgorithm.ES256)
			.compact()
	}

	private val privateKey: PrivateKey
		get() {
			Security.addProvider(BouncyCastleProvider())
			val converter: JcaPEMKeyConverter = JcaPEMKeyConverter().setProvider("BC")

			try {
				val privateKeyBytes: ByteArray = Base64.getDecoder().decode(appleProperties.p8)
				val privateKeyInfo = PrivateKeyInfo.getInstance(privateKeyBytes)
				return converter.getPrivateKey(privateKeyInfo)
			} catch (e: Exception) {
				throw CustomException(ErrorCode.APPLE_PRIVATE_KEY_ENCODING_FAILED)
			}
		}

	/**
	 * Apple로부터 받은 idToken 검증하고 identifier를 추출합니다.
	 *
	 * @param authorizationCode
	 * @return
	 */
	fun authenticateFromApple(authorizationCode: String): SocialClientResponse {
		val tokenRequest: AppleTokenRequest =
			AppleTokenRequest.of(
				authorizationCode,
				appleProperties.dev.clientId,
				generateAppleClientSecret(),
				APPLE_GRANT_TYPE,
			)
		val appleTokenResponse: AppleTokenResponse = getAppleToken(tokenRequest)

		println("appleTokenResponse: $appleTokenResponse")
		val keys: Array<AppleKeyResponse> = retrieveAppleKeys()
		try {
			val tokenParts: List<String> = appleTokenResponse.idToken.split('.')
			val headerPart = String(Base64.getDecoder().decode(tokenParts[0]))
			val headerNode = objectMapper.readTree(headerPart)
			val kid = headerNode["kid"].asText()
			val alg = headerNode["alg"].asText()

			val matchedKey: AppleKeyResponse =
				Arrays.stream(keys)
					.filter { key -> key.kid == kid && key.alg == alg }
					.findFirst() // 일치하는 키가 없음 => 만료된 토큰 or 이상한 토큰 => throw
					.orElseThrow { InvalidParameterException() }

			val claims: Claims =
				parseIdentifierFromAppleToken(matchedKey, appleTokenResponse.idToken)

			val oauthId: String = claims.get("sub", String::class.java)
			val email: String = claims.get("email", String::class.java)

			return SocialClientResponse(email, oauthId)
		} catch (ex: Exception) {
			throw CustomException(ErrorCode.INTERNAL_SERVER_ERROR)
		}
	}

	private fun retrieveAppleKeys(): Array<AppleKeyResponse> {
		val keyListResponse: AppleKeyListResponse =
			restClient
				.get()
				.uri(APPLE_JWK_SET_URL)
				.header(HttpHeaders.CONTENT_TYPE, APPLICATION_URLENCODED)
				.exchange<AppleKeyListResponse> { _, response ->
					if (!response.statusCode.is2xxSuccessful) {
						throw CustomException(
							ErrorCode.APPLE_KEY_CLIENT_FAILED,
						)
					}
					Objects.requireNonNull(
						response.bodyTo(AppleKeyListResponse::class.java),
					)!!
				}

		return keyListResponse.keys
	}

	@Throws(JsonProcessingException::class, ParseException::class, JOSEException::class)
	private fun parseIdentifierFromAppleToken(
		matchedKey: AppleKeyResponse,
		accessToken: String,
	): Claims {
		val keyData: Key =
			JWK.parse(objectMapper.writeValueAsString(matchedKey)).toRSAKey().toRSAPublicKey()
		val parsedClaims: Jws<Claims> =
			Jwts.parserBuilder().setSigningKey(keyData).build().parseClaimsJws(accessToken)

		return parsedClaims.body
	}
}

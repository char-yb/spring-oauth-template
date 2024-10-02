package com.social.demo.presentation.config.security

import com.social.demo.application.auth.service.JwtTokenProvider
import com.social.demo.presentation.config.filter.JwtAuthenticationFilter
import com.social.demo.util.cookie.CookieUtil
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.security.config.Customizer
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.*
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class WebSecurityConfig(
	private val jwtTokenProvider: JwtTokenProvider,
	private val cookieUtil: CookieUtil,
) {

	@Bean
	@Throws(Exception::class)
	fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
		apiSecurityFilterChain(http)

		http.authorizeHttpRequests { authorize ->
			authorize
				.requestMatchers("/service-actuator/**")
				.permitAll() // Actuator
				.requestMatchers("/auth/**")
				.permitAll() // Auth endpoints
				.anyRequest()
				.authenticated()
		}
			.exceptionHandling { exception: ExceptionHandlingConfigurer<HttpSecurity?> ->
				exception.authenticationEntryPoint {
						_: HttpServletRequest?,
						response: HttpServletResponse,
						_: AuthenticationException?,
					->
					response.status = 401
				}
			}

		http.addFilterBefore(
			jwtAuthenticationFilter(jwtTokenProvider, cookieUtil),
			UsernamePasswordAuthenticationFilter::class.java,
		)
		return http.build()
	}

	@Throws(Exception::class)
	private fun apiSecurityFilterChain(http: HttpSecurity) {
		http
			.httpBasic { httpBasicConfigurer -> httpBasicConfigurer.disable() }
			.formLogin { formLoginConfigurer -> formLoginConfigurer.disable() }
			.csrf { csrfConfigurer -> csrfConfigurer.disable() }
			.cors(withDefaults())
			.sessionManagement { sessionManagementConfigurer ->
				sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			}
	}

	@Bean
	fun passwordEncoder(): PasswordEncoder {
		return BCryptPasswordEncoder()
	}

	@Throws(Exception::class)
	private fun defaultFilterChain(http: HttpSecurity) {
		http.httpBasic { obj: HttpBasicConfigurer<HttpSecurity> -> obj.disable() }
			.formLogin { obj: FormLoginConfigurer<HttpSecurity> -> obj.disable() }
			.cors(Customizer.withDefaults())
			.csrf { obj: CsrfConfigurer<HttpSecurity> -> obj.disable() }
			.sessionManagement { session: SessionManagementConfigurer<HttpSecurity?> ->
				session.sessionCreationPolicy(
					SessionCreationPolicy.STATELESS,
				)
			}
	}

	@Bean
	fun corsConfigurationSource(): CorsConfigurationSource {
		val configuration = CorsConfiguration()
		// TODO: CORS 임시 전체 허용
		configuration.addAllowedOriginPattern("*")

		configuration.addAllowedHeader("*")
		configuration.addAllowedMethod("*")
		configuration.allowCredentials = true
		configuration.addExposedHeader(HttpHeaders.SET_COOKIE)

		val source = UrlBasedCorsConfigurationSource()
		source.registerCorsConfiguration("/**", configuration)
		return source
	}

	@Bean
	fun jwtAuthenticationFilter(
		jwtTokenProvider: JwtTokenProvider,
		cookieUtil: CookieUtil,
	): JwtAuthenticationFilter {
		return JwtAuthenticationFilter(jwtTokenProvider, cookieUtil)
	}
}

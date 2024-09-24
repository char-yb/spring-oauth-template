package com.social.demo.common.annotation

import org.springframework.stereotype.Component

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Component
annotation class WebAdapter()

package com.social.demo.util

import org.slf4j.LoggerFactory

/* Logger 인스턴스
 * Logger 인스턴스를 생성하는 함수
 * inline 함수로 선언하여 Logger 인스턴스를 생성하는 코드를 호출하는 위치에 삽입
 * reified는 코틀린에서 제네릭 타입 파라미터를 실체화(reify)하는 키워드
 * 일반적으로, 제네릭 타입은 런타임에 지워지는데, 이를 타입 소거(type erasure)라고 한다. 즉, 런타임에는 제네릭 타입 정보를 알 수 없다.

 * 사용 예시
 * val log = logger()
 * log.info("Hello, World!")
 */
inline fun <reified T> T.logger() = LoggerFactory.getLogger(T::class.java)!!

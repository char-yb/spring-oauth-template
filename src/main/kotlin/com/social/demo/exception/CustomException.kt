package com.social.demo.exception

class CustomException(val errorCode: ErrorCode) : RuntimeException(errorCode.message)

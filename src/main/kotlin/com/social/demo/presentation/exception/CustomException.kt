package com.social.demo.presentation.exception

class CustomException(val errorCode: ErrorCode) : RuntimeException(errorCode.message)

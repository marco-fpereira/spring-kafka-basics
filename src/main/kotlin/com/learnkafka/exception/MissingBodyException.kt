package com.learnkafka.exception

import com.learnkafka.exception.dto.ErrorResponseDTO

class MissingBodyException(
    override val message: String? = null,
    val errorList: List<ErrorResponseDTO>,
    override val cause: Throwable? = null
) : RuntimeException(message)

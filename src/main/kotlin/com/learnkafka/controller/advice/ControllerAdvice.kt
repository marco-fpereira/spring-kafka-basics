package com.learnkafka.controller.advice

import com.learnkafka.exception.MissingBodyException
import com.learnkafka.exception.dto.ErrorResponseDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler

@org.springframework.web.bind.annotation.ControllerAdvice
class ControllerAdvice {

    @ExceptionHandler(MissingBodyException::class)
    fun handleMissingBodyException(ex: MissingBodyException)
    : ResponseEntity<List<ErrorResponseDTO>> = ResponseEntity.badRequest().body(ex.errorList)
}
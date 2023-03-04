package com.learnkafka.utils

import com.learnkafka.exception.dto.ErrorResponseDTO
import org.springframework.validation.BindingResult

class ErrorMapper {
    companion object {
        fun mapBindingResultErrors(result: BindingResult): MutableList<ErrorResponseDTO> {
            val fieldErrorList: MutableList<ErrorResponseDTO> = mutableListOf()
            result.fieldErrors.forEach {
                fieldErrorList.add(
                    ErrorResponseDTO(
                        field = it.field,
                        message = it.defaultMessage.orEmpty()
                    )
                )
            }
            return fieldErrorList
        }
    }

}

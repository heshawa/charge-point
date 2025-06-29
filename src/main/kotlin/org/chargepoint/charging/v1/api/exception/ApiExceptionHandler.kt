package org.chargepoint.charging.v1.api.exception

import jakarta.validation.ConstraintViolationException
import org.chargepoint.charging.v1.api.dto.ChargingResponse
import org.chargepoint.charging.v1.api.service.ChargingService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiExceptionHandler(private val chargingService: ChargingService, 
                          private val requestAuditContext: RequestAuditContext) {
    
    var log : Logger = LoggerFactory.getLogger(ApiExceptionHandler::class.java)
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleValidationException(exception: ConstraintViolationException): ResponseEntity<ChargingResponse> {
        log.error(String.format("Invalid argument: %s",exception.message?:"Unknown error"),exception)
        return ResponseEntity.badRequest().body(ChargingResponse(false,exception.constraintViolations.map { it.message }.joinToString ( "," )))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentException(exception: MethodArgumentNotValidException):ResponseEntity<ChargingResponse>{
        val errorMessages = exception.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage?:"Invalid error") }.values
        log.error("Invalid argument: {}",errorMessages,exception)
        return ResponseEntity.badRequest().body(ChargingResponse(false, errorMessages.toString()))
    }

    @ExceptionHandler(ServiceRequestException::class)
    fun handleServiceRequestException(exception: ServiceRequestException
    ):ResponseEntity<ChargingResponse>{
        val requestBody = requestAuditContext.chargingRequest
        if(requestBody != null){
            chargingService.persistErrorRequestInDB(requestBody, "Error: ${ exception.message ?: "Unexpected error" }")
        }

        log.error("Invalid argument: {}",exception.message,exception)
        return ResponseEntity.badRequest().body(ChargingResponse(false, exception.message?:"Unexpected error"))
    }


}
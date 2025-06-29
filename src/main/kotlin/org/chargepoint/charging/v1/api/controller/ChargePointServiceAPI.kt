package org.chargepoint.charging.v1.api.controller

import jakarta.validation.Valid
import kotlinx.datetime.Clock
import org.chargepoint.charging.v1.api.dto.ChargingRequest
import org.chargepoint.charging.v1.api.dto.ChargingResponse
import org.chargepoint.charging.v1.api.dto.RequestStatus
import org.chargepoint.charging.v1.api.dto.ServiceRequestContext
import org.chargepoint.charging.v1.api.exception.RequestAuditContext
import org.chargepoint.charging.v1.api.exception.ServiceRequestException
import org.chargepoint.charging.v1.api.service.ChargingService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.util.*

@RestController
@RequestMapping("/chargepoint/v1/api")
@Validated
class ChargePointServiceAPI(
    private val chargingService: ChargingService, 
    private var requestAuditContext: RequestAuditContext) {
    val log : Logger = LoggerFactory.getLogger(ChargePointServiceAPI::class.java)
    
    @PostMapping("/charge")
    fun vehicleChargeRequest(@RequestBody @Valid request : ChargingRequest):ResponseEntity<ChargingResponse>{
        try {
            requestAuditContext.chargingRequest = request
        } catch (exception : IllegalStateException){
            log.warn("Request audit context is not available. " +
                    "Client Id: ${request.driverId} Station Id: ${request.requestedStationId}")
        }
        val chargingRequest = try{
            ServiceRequestContext(
                UUID.fromString(request.driverId),
                UUID.fromString(request.requestedStationId),
                request.callbackUrl
            )
        }catch (exception: IllegalArgumentException){
            chargingService.persistErrorRequestInDB(request)
            throw ServiceRequestException(exception.message?:"Unexpected error occurred",exception)
        }

        chargingRequest.requestCorrelationId = UUID.randomUUID()
        
        chargingService.persistRequestInDB(chargingRequest)
        
        chargingService.publishServiceRequestToKafka(chargingRequest)
        
        chargingRequest.status = RequestStatus.PUBLISHED
        chargingRequest.lastModifiedTime = Clock.System.now().toString()
        chargingService.persistRequestInDB(chargingRequest)
        
        return ResponseEntity.ok().body(ChargingResponse())
    }
    
    //API for dev testing. Create stations, clients and vehicles
    @GetMapping("/populateData")
    fun populateData(){
        chargingService.insertSystemData()
    }
}
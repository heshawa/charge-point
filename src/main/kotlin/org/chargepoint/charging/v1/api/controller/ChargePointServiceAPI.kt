package org.chargepoint.charging.v1.api.controller

import jakarta.validation.Valid
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import org.chargepoint.charging.v1.api.dto.ChargingRequest
import org.chargepoint.charging.v1.api.dto.ChargingResponse
import org.chargepoint.charging.v1.api.dto.RequestStatus
import org.chargepoint.charging.v1.api.exception.RequestAuditContext
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
import java.lang.IllegalStateException

@RestController
@RequestMapping("/chargepoint/v1/api")
@Validated
class ChargePointServiceAPI(
    private val chargingService: ChargingService, 
    private var requestAuditContext: RequestAuditContext,
    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) {
    val log : Logger = LoggerFactory.getLogger(ChargePointServiceAPI::class.java)
    
    @PostMapping("/charge")
    fun vehicleChargeRequest(@RequestBody @Valid request : ChargingRequest):ResponseEntity<ChargingResponse>{
        try {
            requestAuditContext.chargingRequest = request
        } catch (exception : IllegalStateException){
            log.warn("Request audit context is not available. " +
                    "Client Id: ${request.driverId} Station Id: ${request.requestedStationId}")
        }

        val chargingRequest = chargingService.createEnrichedServiceRequest(request)

        chargingService.persistRequestInDBAsync(chargingRequest)
        val job = chargingService.publishServiceRequestToKafkaAsync(chargingRequest)

        coroutineScope.launch {
            try {
                job.join()
                chargingRequest.status = RequestStatus.PUBLISHED
                chargingRequest.lastModifiedTime = Clock.System.now().toString()
                chargingService.persistRequestInDBAsync(chargingRequest)
            } catch (exception : Exception){
                log.error("Kafka publish failed. Client Id: ${chargingRequest.clientUUID}, Station Id: ${chargingRequest.stationUUID}")
                chargingService.persistRequestInDBAsync(chargingRequest)
            }
        }
        return ResponseEntity.ok().body(ChargingResponse())
    }
    
    //API for dev testing. Create stations, clients and vehicles
    @GetMapping("/populateData")
    fun populateData(){
        chargingService.insertSystemData()
    }
}
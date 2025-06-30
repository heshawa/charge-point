package org.chargepoint.charging.v1.api.controller

import jakarta.validation.Valid
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import org.chargepoint.charging.v1.api.dto.CallbackRequestBody
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
import kotlin.random.Random

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
            //Add request to record request body on failure
            requestAuditContext.chargingRequest = request
        } catch (exception : IllegalStateException){
            log.warn("Request audit context is not available. " +
                    "Client Id: ${request.driverId} Station Id: ${request.requestedStationId}")
        }

        val chargingRequest = chargingService.createEnrichedServiceRequest(request)

        //Async process after enrichment complete
        coroutineScope.launch {
            chargingService.persistRequestInDBAsync(chargingRequest)
        }
        
        val job = coroutineScope.launch {
            chargingService.publishServiceRequestToKafkaAsync(chargingRequest)
        }

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
    
    @PostMapping("/callback")
    fun processRequestCompletion(@RequestBody request : CallbackRequestBody) : ResponseEntity<Map<String,Boolean>>{
        return ResponseEntity.ok(mapOf("success" to Random.nextBoolean()))
    }

    //API for dev testing. Create stations, clients and vehicles
    @GetMapping("/populateData")
    fun populateData(){
        chargingService.insertSystemData()
    }
}
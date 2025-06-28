package org.chargepoint.charging.v1.api.controller

import org.chargepoint.charging.v1.api.dto.ChargingRequest
import org.chargepoint.charging.v1.api.dto.ChargingResponse
import org.chargepoint.charging.v1.api.dto.ServiceRequestContext
import org.chargepoint.charging.v1.api.service.ChargingService
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/chargepoint/v1/api")
@Validated
class ChargePointServiceAPI(
    private val chargingService: ChargingService
) {
    
    @PostMapping("/charge")
    fun vehicleChargeRequest(@RequestBody request : ChargingRequest):ResponseEntity<ChargingResponse>{
        val chargingRequest = ServiceRequestContext(
            UUID.fromString(request.driverId), 
            UUID.fromString(request.requestedStationId)
        )
        chargingRequest.requestCorrelationId = UUID.randomUUID()
        
        chargingService.publishServiceRequestToKafka(chargingRequest)
        
        return ResponseEntity.ok().body(ChargingResponse())
    }
}
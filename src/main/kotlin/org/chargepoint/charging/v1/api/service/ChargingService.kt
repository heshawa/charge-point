package org.chargepoint.charging.v1.api.service

import kotlinx.coroutines.Job
import org.chargepoint.charging.v1.api.dto.ChargingRequest
import org.chargepoint.charging.v1.api.dto.ServiceRequestContext

interface ChargingService{
    fun publishServiceRequestToKafka(request: ServiceRequestContext)

    fun publishServiceRequestToKafkaAsync(request:ServiceRequestContext) : Job
    
    fun persistRequestInDB(request: ServiceRequestContext)

    fun persistRequestInDBAsync(request: ServiceRequestContext, error : String = "") : Job

    fun persistErrorRequestInDB(request: ChargingRequest, error:String = "")

    fun persistErrorRequestInDBAsync(request: ChargingRequest, error:String) : Job

    fun createEnrichedServiceRequest(request: ChargingRequest) : ServiceRequestContext
    
    //Function for dev testing purposes
    fun insertSystemData()
    
}
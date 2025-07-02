package org.chargepoint.charging.v1.api.service

import kotlinx.coroutines.Job
import org.chargepoint.charging.v1.api.dto.ChargingRequest
import org.chargepoint.charging.v1.api.dto.ServiceRequestContext

interface ChargingService{
    suspend fun publishServiceRequestToKafkaAsync(request:ServiceRequestContext) : Job
    
    suspend fun persistRequestInDBAsync(request: ServiceRequestContext, error : Exception? = null) : Job

    fun persistErrorRequestInDB(request: ChargingRequest, error:String = "")

    fun createEnrichedServiceRequest(request: ChargingRequest) : ServiceRequestContext
    
    //Function for dev testing purposes
    fun insertSystemData()
    
}
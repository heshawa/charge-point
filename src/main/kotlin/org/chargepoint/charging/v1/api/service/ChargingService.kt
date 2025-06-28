package org.chargepoint.charging.v1.api.service

import org.chargepoint.charging.v1.api.dto.ServiceRequestContext

interface ChargingService{
    fun publishServiceRequestToKafka(request: ServiceRequestContext)
    
    fun persistRequestInDB(request: ServiceRequestContext)
    
}
package org.chargepoint.charging.database.dao

import org.chargepoint.charging.v1.api.dto.ChargingRequest
import org.chargepoint.charging.v1.api.dto.ServiceRequestContext

interface ChargeRequestDAO {
    fun saveRequestDataOnError(chargingRequest: ChargingRequest)
    
    fun saveRequestData(serviceRequestContext: ServiceRequestContext)
    
    fun insertSystemData()    
}
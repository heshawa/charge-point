package org.chargepoint.charging.database.dao

import org.chargepoint.charging.v1.api.dto.ChargingRequest
import org.chargepoint.charging.v1.api.dto.ServiceRequestContext

interface ChargeRequestDAO {
    fun saveRequestDataOnError(chargingRequest: ChargingRequest, error:String = "")
    
    fun saveRequestData(serviceRequestContext: ServiceRequestContext, error:String = "")
    
    fun insertSystemData()    
}
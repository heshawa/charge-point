package org.chargepoint.charging.database.dao

import kotlinx.datetime.Instant
import org.chargepoint.charging.database.entity.ChargeRequest
import org.chargepoint.charging.database.repository.ChargeRequestRepository
import org.chargepoint.charging.v1.api.dto.ChargingRequest
import org.chargepoint.charging.v1.api.dto.ServiceRequestContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ChargeRequestDAOImpl(private val chargeRequestRepository: ChargeRequestRepository) : ChargeRequestDAO {
    override fun saveRequestDataOnError(chargingRequest: ChargingRequest) {
        TODO("Not yet implemented")
    }

    override fun saveRequestData(serviceRequestContext: ServiceRequestContext) {
        var requestData : ChargeRequest? = ChargeRequest()
        
        requestData?.correlationId = serviceRequestContext.requestCorrelationId.toString()
        requestData?.requestedTime = Instant.parse(serviceRequestContext.timeStamp)
        requestData?.clientUUID = serviceRequestContext.clientUUID.toString()
        requestData?.status = serviceRequestContext.status.statusId
        requestData?.stationUUID = serviceRequestContext.stationUUID.toString()
        requestData?.lastModifiedTime = serviceRequestContext.lastModifiedTime?.let { Instant.parse(it) }

        requestData?.let { chargeRequestRepository.save(it) }
    }
}
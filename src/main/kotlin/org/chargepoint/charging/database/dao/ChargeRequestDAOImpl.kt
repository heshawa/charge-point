package org.chargepoint.charging.database.dao

import kotlinx.datetime.Instant
import org.chargepoint.charging.database.entity.ChargeRequest
import org.chargepoint.charging.database.entity.ChargingStation
import org.chargepoint.charging.database.entity.EvClient
import org.chargepoint.charging.database.repository.ChargeRequestRepository
import org.chargepoint.charging.database.repository.ChargingStationRepository
import org.chargepoint.charging.database.repository.ClientRepository
import org.chargepoint.charging.v1.api.dto.ChargingRequest
import org.chargepoint.charging.v1.api.dto.RequestStatus
import org.chargepoint.charging.v1.api.dto.ServiceRequestContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ChargeRequestDAOImpl(private val chargeRequestRepository: ChargeRequestRepository, 
                           //Following 3 repositories are using only to add data. 
                           //May have to implement separate DAO based on usage
                           private val chargingStationRepository: ChargingStationRepository, 
                           private val evClientRepository: ClientRepository
) : ChargeRequestDAO {
    override fun saveRequestDataOnError(chargingRequest: ChargingRequest, error:String) {
        val requestData : ChargeRequest? = ChargeRequest()
        
        requestData?.clientUUID = chargingRequest.driverId
        requestData?.stationUUID = chargingRequest.requestedStationId
        requestData?.callbackUrl = chargingRequest.callbackUrl
        requestData?.status = RequestStatus.FAILED.statusId

        error.takeIf { it.isNotEmpty() }?.let { requestData?.remarks = error }
        requestData?.let { chargeRequestRepository.save(it) }
    }

    override fun saveRequestData(serviceRequestContext: ServiceRequestContext, error: String) {
        val requestData : ChargeRequest? = ChargeRequest()
        
        requestData?.correlationId = serviceRequestContext.requestCorrelationId.toString()
        requestData?.requestedTime = Instant.parse(serviceRequestContext.timeStamp)
        requestData?.clientUUID = serviceRequestContext.clientUUID.toString()
        requestData?.status = serviceRequestContext.status.statusId
        requestData?.stationUUID = serviceRequestContext.stationUUID.toString()
        requestData?.callbackUrl = serviceRequestContext.callbackUrl
        requestData?.lastModifiedTime = serviceRequestContext.lastModifiedTime?.let { Instant.parse(it) }

        error.takeIf { it.isNotEmpty() }?.let { requestData?.remarks = it }

        requestData?.let { chargeRequestRepository.save(it) }
    }

    override fun insertSystemData() {

        val chargingStation1 = ChargingStation()
        val chargingStation2 = ChargingStation()
        val chargingStation3 = ChargingStation()
        val chargingStation4 = ChargingStation()

        val evClient1 = EvClient()
        val evClient2 = EvClient()
        val evClient3 = EvClient()
        val evClient4 = EvClient()

        evClient1.nationalId = "11224454564-A"

        evClient2.nationalId = "344242332234-A"

        evClient3.nationalId = "6643342434-A"

        evClient4.nationalId = "232002339342-A"

        chargingStationRepository.saveAll(
            mutableListOf(chargingStation1,chargingStation2,chargingStation3,chargingStation4)
        )
        evClientRepository.saveAll(mutableListOf(evClient1, evClient2, evClient3, evClient4))
    }
}
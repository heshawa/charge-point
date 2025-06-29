package org.chargepoint.charging.database.dao

import kotlinx.datetime.Instant
import org.chargepoint.charging.database.entity.ChargeRequest
import org.chargepoint.charging.database.entity.ChargingStation
import org.chargepoint.charging.database.entity.ElectricVehicle
import org.chargepoint.charging.database.entity.EvClient
import org.chargepoint.charging.database.repository.ChargeRequestRepository
import org.chargepoint.charging.database.repository.ChargingStationRepository
import org.chargepoint.charging.database.repository.ClientRepository
import org.chargepoint.charging.database.repository.ElectricVehicleRepository
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
                           private val electricVehicleRepository: ElectricVehicleRepository, 
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

        chargingStation1.stationLocation = "Radstadt"
        chargingStation1.contactNumber = "+1 33 55552 2322"
        chargingStation1.stationType = 0

        chargingStation2.stationLocation = "Munich, Germany"
        chargingStation2.contactNumber = "+1 34 454545 2322"
        chargingStation2.stationType = 1

        chargingStation3.stationLocation = "Salzburg"
        chargingStation3.contactNumber = "+1 33 434346 2322"
        chargingStation3.stationType = 2

        chargingStation4.stationLocation = "Graz"
        chargingStation4.contactNumber = "+1 33 22334 6557"
        chargingStation4.stationType = 0

        val evClient1 = EvClient()
        val evClient2 = EvClient()
        val evClient3 = EvClient()
        val evClient4 = EvClient()

        evClient1.firstName = "Heshawa"
        evClient1.lastName = "De Silva"
        evClient1.nationalId = "11224454564-A"
        evClient1.contactNUmber = "+971522950622"

        evClient2.firstName = "Ed"
        evClient2.lastName = "Wilson"
        evClient2.nationalId = "344242332234-A"

        evClient3.firstName = "Aida"
        evClient3.lastName = "Houxa"
        evClient3.nationalId = "6643342434-A"
        evClient3.contactNUmber = "+447782545455"

        evClient4.firstName = "Jem"
        evClient4.lastName = "Theresa"
        evClient4.nationalId = "232002339342-A"
        evClient4.contactNUmber = "+92834788622"

        chargingStationRepository.saveAll(
            mutableListOf(chargingStation1,chargingStation2,chargingStation3,chargingStation4)
        )
        val savedClients = evClientRepository.saveAll(mutableListOf(evClient1, evClient2, evClient3, evClient4))

        val electricVehicle1 = ElectricVehicle()
        val electricVehicle2 = ElectricVehicle()
        val electricVehicle3 = ElectricVehicle()
        val electricVehicle4 = ElectricVehicle()

        electricVehicle1.chassisNumber = "2112-CV23232-22-XX"
        electricVehicle1.brand = "Toyota"
        electricVehicle1.model = "Corolla"
        electricVehicle1.registrationNumber = "EV 23452"
        electricVehicle1.customerVehicle = savedClients.get(0)

        electricVehicle2.chassisNumber = "2112-DR45454-99-XX"
        electricVehicle2.brand = "FORD"
        electricVehicle2.model = "Raptor"
        electricVehicle2.registrationNumber = "EV 45434"
        electricVehicle2.customerVehicle = savedClients.get(0)

        electricVehicle3.chassisNumber = "2332-CV23442-22-XC"
        electricVehicle3.brand = "Toyota"
        electricVehicle3.model = "Tundra"
        electricVehicle3.registrationNumber = "EV 54678"
        electricVehicle3.customerVehicle = savedClients.get(2)

        electricVehicle4.chassisNumber = "2112-FD44444-22-XX"
        electricVehicle4.brand = "Lexus"
        electricVehicle4.model = "G9123"
        electricVehicle4.registrationNumber = "EV 11144"
        electricVehicle4.customerVehicle = savedClients.get(1)

        electricVehicleRepository.saveAll(
            mutableListOf(electricVehicle1,electricVehicle2,electricVehicle3,electricVehicle4))
    }
}
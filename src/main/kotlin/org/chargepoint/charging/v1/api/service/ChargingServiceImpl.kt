package org.chargepoint.charging.v1.api.service

import kotlinx.coroutines.*
import org.chargepoint.charging.database.dao.ChargeRequestDAO
import org.chargepoint.charging.database.dao.ClientDAO
import org.chargepoint.charging.database.dao.StationDAO
import org.chargepoint.charging.v1.api.dto.ChargingRequest
import org.chargepoint.charging.v1.api.dto.ServiceRequestContext
import org.chargepoint.charging.v1.api.exception.ServiceRequestException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.lang.IllegalArgumentException
import java.util.*

@Service
class ChargingServiceImpl(private val kafkaTemplate: KafkaTemplate<String,ServiceRequestContext>,
                          @Value("\${kafka.topics.charge-point-server-request}")
                          private val requestPublishTopic:String,
                          private val chargeRequestDAO: ChargeRequestDAO,
                          private val clientDAO: ClientDAO,
                          private val stationDAO: StationDAO,
                          private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
)  : ChargingService{
    
    val log : Logger = LoggerFactory.getLogger(ChargingServiceImpl::class.java)

    override fun publishServiceRequestToKafka(request:ServiceRequestContext) {
        kafkaTemplate.send(requestPublishTopic,request.requestCorrelationId.toString(),request)
        log.info("Request details published successfully. request identifier: {}",request.requestCorrelationId)
    }

    override fun publishServiceRequestToKafkaAsync(request:ServiceRequestContext) : Job {
        return coroutineScope.launch {
            kafkaTemplate.send(requestPublishTopic,request.requestCorrelationId.toString(),request)
            log.info("Request details published successfully. request identifier: {}",request.requestCorrelationId)
        }
    }

    override fun persistRequestInDB(request: ServiceRequestContext) {
        chargeRequestDAO.saveRequestData(request)
        log.info("Request data saved to database successfully. Request identifier: {}",request.requestCorrelationId)
    }

    override fun persistRequestInDBAsync(request: ServiceRequestContext,  error : String) : Job {
        return coroutineScope.launch {
            chargeRequestDAO.saveRequestData(request)
            log.info("Request data saved to database successfully. Request identifier: {}",request.requestCorrelationId)
        }
    }

    override fun persistErrorRequestInDB(request: ChargingRequest,error:String) {
        chargeRequestDAO.saveRequestDataOnError(request, error)
    }

    override fun persistErrorRequestInDBAsync(request: ChargingRequest,error:String) : Job {
        return coroutineScope.launch {
            chargeRequestDAO.saveRequestDataOnError(request, error)
        }
    }

    override fun createEnrichedServiceRequest(request: ChargingRequest) : ServiceRequestContext{
        val chargingRequest = try{
            ServiceRequestContext(
                UUID.fromString(request.driverId),
                UUID.fromString(request.requestedStationId),
                request.callbackUrl
            )
        }catch (exception: IllegalArgumentException){
            throw ServiceRequestException(exception.message?:"Unexpected error occurred",exception)
        }

        isRegisteredClient(chargingRequest.clientUUID)
        isValidChargingStation(chargingRequest.stationUUID)
        //TODO: Validate callback URL

        chargingRequest.requestCorrelationId = UUID.randomUUID()

        return chargingRequest
    }

    private fun isRegisteredClient(clientId: UUID) : Boolean {
        val registeredClient = clientDAO.getClient(clientId)
        return registeredClient != null
    }

    private fun isValidChargingStation(stationId: UUID) : Boolean {
        val registeredStation = stationDAO.getStation(stationId)
        return registeredStation != null
    }

    //Function for dev testing purposes
    override fun insertSystemData() {
        chargeRequestDAO.insertSystemData()
    }
}
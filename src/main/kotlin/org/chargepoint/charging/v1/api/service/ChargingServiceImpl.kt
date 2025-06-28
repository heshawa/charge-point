package org.chargepoint.charging.v1.api.service

import org.apache.kafka.clients.producer.ProducerRecord
import org.chargepoint.charging.database.dao.ChargeRequestDAO
import org.chargepoint.charging.v1.api.dto.ChargingRequest
import org.chargepoint.charging.v1.api.dto.ServiceRequestContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class ChargingServiceImpl(private val kafkaTemplate: KafkaTemplate<String,ServiceRequestContext>, 
                          @Value("\${kafka.topics.charge-point-server-request}")
                          private val requestPublishTopic:String, private val chargeRequestDAO: ChargeRequestDAO)  : ChargingService{
    
    val log : Logger = LoggerFactory.getLogger(ChargingServiceImpl::class.java)
    
    override fun publishServiceRequestToKafka(request:ServiceRequestContext) {
        kafkaTemplate.send(requestPublishTopic,request.requestCorrelationId.toString(),request)
        log.info("Request details published successfully. request identifier: {}",request.requestCorrelationId)
    }

    override fun persistRequestInDB(request: ServiceRequestContext) {
        chargeRequestDAO.saveRequestData(request)
        log.info("Request data saved to database successfully. Request identifier: {}",request.requestCorrelationId)
    }

    override fun persistErrorRequestInDB(request: ChargingRequest) {
        chargeRequestDAO.saveRequestDataOnError(request)
    }
}
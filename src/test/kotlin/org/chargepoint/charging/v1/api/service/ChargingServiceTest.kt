package org.chargepoint.charging.v1.api.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.chargepoint.charging.database.dao.ChargeRequestDAO
import org.chargepoint.charging.database.dao.ClientDAO
import org.chargepoint.charging.database.dao.StationDAO
import org.chargepoint.charging.database.entity.ChargingStation
import org.chargepoint.charging.database.entity.EvClient
import org.chargepoint.charging.v1.api.dto.ChargingRequest
import org.chargepoint.charging.v1.api.dto.RequestStatus
import org.chargepoint.charging.v1.api.dto.ServiceRequestContext
import org.chargepoint.charging.v1.api.exception.ServiceRequestException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.kafka.core.KafkaTemplate
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class ChargingServiceTest {
    
    private val kafkaTemplate: KafkaTemplate<String, ServiceRequestContext> = mockk(relaxed = true)
    private val chargeRequestDAO: ChargeRequestDAO = mockk(relaxed = true)
    private val clientDAO: ClientDAO = mockk()
    private val stationDAO: StationDAO = mockk()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var service: ChargingService
    
    private val callbackUrl = "http://localhost:8080/chargepoint/v1/api/callback"
    private val topicName = "charge-point-service-request"
    private val message = ServiceRequestContext(UUID.randomUUID(), UUID.randomUUID(), callbackUrl)

    @BeforeEach
    fun setup(){
        service = ChargingServiceImpl(
            kafkaTemplate,topicName,chargeRequestDAO, clientDAO, stationDAO
        )
    }

    @AfterEach
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun `publishServiceRequestToKafkaAsync should publish message to Kafka`() = runTest(testDispatcher){
        message.requestCorrelationId = UUID.randomUUID()
        
        service.publishServiceRequestToKafkaAsync(message).join()
        
        verify { kafkaTemplate.send(topicName, message.requestCorrelationId.toString(), message) }
    }

    @Test
    fun `persistRequestInDBAsync should persist the data in database`() = runTest(testDispatcher){

        message.requestCorrelationId = UUID.randomUUID()
        service.persistRequestInDBAsync(message).join()

        verify { chargeRequestDAO.saveRequestData(message) }
    }

    @Test
    fun `persistRequestInDBAsync should persist the data in database with error message`() = runTest(testDispatcher){
        val error = ServiceRequestException("Processing error")
        message.requestCorrelationId = UUID.randomUUID()

        service.persistRequestInDBAsync(message,error).join()

        verify { chargeRequestDAO.saveRequestData(message,error.message?:"") }
    }

    @Test
    fun `persistRequestInDB should persist the data in database with error message`() = runTest(testDispatcher){
        val request = ChargingRequest(UUID.randomUUID().toString(),UUID.randomUUID().toString(),callbackUrl)
        val errorMessage = "Unexpected processing error"

        service.persistErrorRequestInDB(request,errorMessage)

        verify { chargeRequestDAO.saveRequestDataOnError(request,errorMessage) }
    }

    @Test
    fun `createEnrichedServiceRequest should return ServiceRequestContext`() {

        val request = ChargingRequest(UUID.randomUUID().toString(),UUID.randomUUID().toString(),callbackUrl)

        every { clientDAO.getClient(UUID.fromString(request.driverId)) } returns mockk<EvClient>()
        every { stationDAO.getStation(UUID.fromString(request.requestedStationId)) } returns mockk<ChargingStation>()

        val enrichedServiceRequest = service.createEnrichedServiceRequest(request)

        Assertions.assertEquals(UUID.fromString(request.driverId),enrichedServiceRequest.clientUUID)
        Assertions.assertEquals(UUID.fromString(request.requestedStationId),enrichedServiceRequest.stationUUID)
        Assertions.assertEquals(RequestStatus.SUBMITTED,enrichedServiceRequest.status)
        Assertions.assertNotNull(enrichedServiceRequest.requestCorrelationId)
        Assertions.assertNotNull(enrichedServiceRequest.requestCorrelationId)
        Assertions.assertTrue(enrichedServiceRequest.lastError==null)

    }

    @Test
    fun `createEnrichedServiceRequest should throw ServiceRequestException for invalid UUID`() {

        val request = ChargingRequest("1122223333",UUID.randomUUID().toString(),callbackUrl)

        val exception = Assertions.assertThrows(Exception::class.java){
            service.createEnrichedServiceRequest(request)
        }
        
        Assertions.assertTrue(exception is ServiceRequestException)
        Assertions.assertTrue(exception.message!!.contains("Unexpected error occurred").not())
    }

}
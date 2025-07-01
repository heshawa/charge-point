package org.chargepoint.charging.v1.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.*
import org.chargepoint.charging.v1.api.dto.ChargingRequest
import org.chargepoint.charging.v1.api.dto.ServiceRequestContext
import org.chargepoint.charging.v1.api.exception.RequestAuditContext
import org.chargepoint.charging.v1.api.service.ChargingService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.util.UUID
import kotlin.test.Test

@WebMvcTest(ChargePointServiceAPI::class)
class ChargePointServiceAPITest {
    
    @Autowired
    private lateinit var mockMvc : MockMvc
    
    @MockkBean
    private lateinit var chargingService: ChargingService
    
    @MockkBean
    private lateinit var requestAuditContext: RequestAuditContext
    
    private val objectMapper = ObjectMapper()

    private val driverId = UUID.randomUUID()
    private val stationId = UUID.randomUUID()
    private val callbackUrl = "http://localhost:8080/chargepoint/v1/api/callback"
    
    private val chargingRequest = ChargingRequest(driverId.toString(),stationId.toString(),callbackUrl)
    
    private val enrichedRequest = ServiceRequestContext(driverId,stationId,callbackUrl)

    @BeforeEach
    fun setup() {
        every { chargingService.createEnrichedServiceRequest(any()) } returns enrichedRequest
        coEvery { chargingService.persistRequestInDBAsync(any()) } just Awaits
        coEvery { chargingService.publishServiceRequestToKafkaAsync(any()) } just Awaits
        every { requestAuditContext.chargingRequest = any() } just Runs
    }

    @AfterEach
    fun teardown() {
        unmockkAll()
    }
    
    @Test
    fun `should return 200 OK when vehicleChargeRequestIsSuccessful`(){
        mockMvc.post("/chargepoint/v1/api/charge"){
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(chargingRequest)
        }.andExpect { 
            status { isOk() }
            content { MediaType.APPLICATION_JSON }
        }
        
        verify { chargingService.createEnrichedServiceRequest(chargingRequest) }
        coVerify { chargingService.persistRequestInDBAsync(enrichedRequest) }
        coVerify { chargingService.publishServiceRequestToKafkaAsync(enrichedRequest) }
    }
}
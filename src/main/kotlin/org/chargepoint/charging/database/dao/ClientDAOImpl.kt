package org.chargepoint.charging.database.dao

import org.chargepoint.charging.database.entity.EvClient
import org.chargepoint.charging.database.repository.ClientRepository
import org.chargepoint.charging.v1.api.exception.ServiceRequestException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class ClientDAOImpl(
    private val clientRepository: ClientRepository
) : ClientDAO {
    
    val log : Logger = LoggerFactory.getLogger(ClientDAOImpl::class.java)
    override fun getClient(clientId: UUID): EvClient {
        return clientRepository.findById(clientId)
            .orElseThrow { 
                log.error("Client id is not existing. Client Id: $clientId ")
                ServiceRequestException("Invalid client Id: $clientId") 
            }
            .also { 
                log.info("Successfully fetched client info. Client Id: $clientId") 
            }
    }
}
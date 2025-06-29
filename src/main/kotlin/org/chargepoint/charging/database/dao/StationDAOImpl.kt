package org.chargepoint.charging.database.dao

import org.chargepoint.charging.database.entity.ChargingStation
import org.chargepoint.charging.database.repository.ChargingStationRepository
import org.chargepoint.charging.v1.api.exception.ServiceRequestException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class StationDAOImpl(
    private val stationRepository: ChargingStationRepository
) : StationDAO {
    
    val log : Logger = LoggerFactory.getLogger(StationDAOImpl::class.java)
    override fun getStation(stationId : UUID) : ChargingStation{
        return stationRepository.findById(stationId)
            .orElseThrow { 
                log.error("Station is not existing. Station Id: $stationId")
                ServiceRequestException("Invalid station Id: $stationId") 
            }
            .also { 
                log.info("Station details fetched successfully. Station Id: $stationId")
            }
    }
}
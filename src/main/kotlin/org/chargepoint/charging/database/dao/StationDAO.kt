package org.chargepoint.charging.database.dao

import org.chargepoint.charging.database.entity.ChargingStation
import java.util.*

interface StationDAO {
    fun getStation(stationId : UUID) : ChargingStation
}
package org.chargepoint.charging.database.dao

import org.chargepoint.charging.database.entity.EvClient
import java.util.UUID

interface ClientDAO {
    fun getClient(clientId : UUID) : EvClient
}
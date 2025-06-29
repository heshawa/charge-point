package org.chargepoint.charging.database.repository

import org.chargepoint.charging.database.entity.ElectricVehicle
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ElectricVehicleRepository : JpaRepository<ElectricVehicle,String> {
}
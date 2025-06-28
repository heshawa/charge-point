package org.chargepoint.charging.database.repository

import org.chargepoint.charging.database.entity.ChargeRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChargeRequestRepository : JpaRepository<ChargeRequest, Long> {
}
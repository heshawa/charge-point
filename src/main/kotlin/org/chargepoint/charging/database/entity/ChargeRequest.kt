package org.chargepoint.charging.database.entity

import jakarta.persistence.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.chargepoint.charging.v1.api.dto.RequestStatus
import java.util.*

@Entity
@Table(name = "charge_request")
class ChargeRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    var id: Long? = null

    @Column(name = "correlation_id")
    var correlationId : String? = null

    @Column(name = "requested_time", nullable = false)
    var requestedTime:Instant = Clock.System.now()
    
    @Column(nullable = false)
    var status: Int = RequestStatus.SUBMITTED.statusId
    
    @Column(name = "client_uuid")
    var clientUUID : String? = null
    
    @Column(name = "station_uuid")
    var stationUUID : String? = null
    
    @Column(name = "callback_url")
    var callbackUrl : String? = null
    
    @Column(name = "last_modified")
    var lastModifiedTime: Instant? = null
}
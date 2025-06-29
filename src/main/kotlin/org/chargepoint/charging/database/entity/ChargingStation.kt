package org.chargepoint.charging.database.entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.UUID

@Entity
@Table(name = "charging_station")
class ChargingStation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(name = "station_id", nullable = false)
    var stationId: UUID? = null
    
    @Column(name = "address", nullable = false)
    var stationLocation: String? = null
    
    @Column(name = "contact_number")
    var contactNumber : String? = null
    
    @Column(name = "station_type", nullable = false)
    var stationType : Int? = null //OWN=0,REGULAR=1,PREMIUM=2
}
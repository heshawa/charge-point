package org.chargepoint.charging.database.entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.UUID

@Entity
@Table(name = "electric_vehicle")
class ElectricVehicle {
    @Id
    @Column(name = "chassis_number", nullable = false)
    var chassisNumber: String? = null
    
    @Column(name = "registration_number", nullable = false, unique = true)
    var registrationNumber: String? = null
    
    @Column(name = "brand")
    var brand: String? = null
    
    @Column(name = "model")
    var model: String? = null
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = ForeignKey(name = "client_id"), nullable = false, name = "client_id")
    var customerVehicle: EvClient? = null
}
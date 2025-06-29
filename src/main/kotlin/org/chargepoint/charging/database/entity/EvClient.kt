package org.chargepoint.charging.database.entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.*

@Entity
@Table(name = "ev_customer")
class EvClient {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(name = "client_id", nullable = false)
    var clientId: UUID? = null
    
    @Column(name = "national_id", nullable = false)
    var nationalId: String? = null
    
    @Column(name = "first_name")
    var firstName: String? = null

    @Column(name = "last_name")
    var lastName: String? = null

    @Column(name = "contact_number")
    var contactNUmber: String? = null
    
    @OneToMany(
        mappedBy = "customerVehicle",
        fetch = FetchType.LAZY,
        orphanRemoval = true,
        cascade = [CascadeType.ALL]
    )
    var ownedVehicle: MutableList<ElectricVehicle> = mutableListOf()
}
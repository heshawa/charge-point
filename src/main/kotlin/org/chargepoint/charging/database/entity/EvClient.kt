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
}
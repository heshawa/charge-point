package org.chargepoint.charging.v1.api.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern

data class ChargingRequest (
    @field:NotEmpty(message = "Driver Id cannot be empty")
    @field:Pattern(regexp = "^[A-Za-z0-9_\\-~.]+$", message = "Invalid driver ID")
    val driverId:String,
    @field:NotEmpty(message = "Station Id cannot be empty")
    @field:Pattern(regexp = "^[A-Za-z0-9_\\-~.]+$", message = "Invalid station ID")
    val requestedStationId:String = "",
//    @field:NotEmpty(message = "Callback url cannot be empty") @field:Pattern(regexp = "^(https?://)?(www\\.)?[a-zA-Z0-9\\-]+(\\.[a-zA-Z]{2,})(:[0-9]{1,5})?(/[a-zA-Z0-9_\\-./?=&%]*)?$",
//        message = "Invalid URL format") 
    @field:NotEmpty(message = "Callback url cannot be empty")
    @field:Pattern(
        regexp = "^(https?://)(localhost|[a-zA-Z0-9.-]+)(:\\d{1,5})?(/[a-zA-Z0-9_\\-./?=&%]*)?$",
        message = "Invalid callback URL format"
    )
    val callbackUrl: String
){
}
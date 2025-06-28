package org.chargepoint.charging.v1.api.dto

import jakarta.validation.constraints.Pattern

data class ChargingRequest (
    @Pattern(regexp = "^[A-Za-z0-9_\\-~.]+$", message = "Invalid driver ID") val driverId:String,
    @Pattern(regexp = "^[A-Za-z0-9_\\-~.]+$", message = "Invalid station ID")val requestedStationId:String = "",
    @Pattern(regexp = "^(https?://)?(www\\.)?[a-zA-Z0-9\\-]+(\\.[a-zA-Z]{2,})(:[0-9]{1,5})?(/[a-zA-Z0-9_\\-./?=&%]*)?$",
        message = "Invalid URL format") val callbackUrl: String
){
}
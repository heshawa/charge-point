package org.chargepoint.charging.v1.api.dto

data class ChargingResponse(
    val success:Boolean=true,
    val message: String = "Request submitted successfully"
) {
}
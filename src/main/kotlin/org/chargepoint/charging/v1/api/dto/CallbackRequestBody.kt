package org.chargepoint.charging.v1.api.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CallbackRequestBody(
    @JsonProperty("driver_token")
    val driverToken : String,
    @JsonProperty("station_id")
    val stationId : String,
    val status : String
)

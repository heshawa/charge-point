package org.chargepoint.charging.v1.api.dto

enum class RequestStatus(val statusId : Int, val value:String) {
    SUBMITTED(1,"submitted"),
    PUBLISHED(2,"published"),
    PROCESSED(3,"processed"),
    FAILED(4, "failed");
}
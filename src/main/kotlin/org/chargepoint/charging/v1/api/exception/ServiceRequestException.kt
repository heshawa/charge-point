package org.chargepoint.charging.v1.api.exception

import java.lang.IllegalArgumentException

class ServiceRequestException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)


}
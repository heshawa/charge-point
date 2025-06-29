package org.chargepoint.charging.v1.api.exception

import org.chargepoint.charging.v1.api.dto.ChargingRequest
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component
import org.springframework.web.context.WebApplicationContext

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
class RequestAuditContext {
    var chargingRequest: ChargingRequest? = null
}

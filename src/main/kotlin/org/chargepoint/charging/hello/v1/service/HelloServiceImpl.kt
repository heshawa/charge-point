package org.chargepoint.charging.hello.v1.service

import org.chargepoint.charging.hello.v1.dto.HelloResponse
import org.springframework.stereotype.Service

@Service
class HelloServiceImpl : HelloService {
    override fun getHelloWorldMessage(userName: String): HelloResponse {
        return HelloResponse(String.format("Hello %s",userName))
    }

}
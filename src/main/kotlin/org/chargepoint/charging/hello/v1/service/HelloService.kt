package org.chargepoint.charging.hello.v1.service

import org.chargepoint.charging.hello.v1.dto.HelloResponse

interface HelloService {
    fun getHelloWorldMessage(userName:String): HelloResponse
}
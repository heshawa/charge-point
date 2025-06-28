package org.chargepoint.charging.hello.v1.dto

import kotlinx.datetime.Clock

data class HelloResponse(val message:String = "Hello world", 
                         val success:Boolean = true,
                         val timeStamp: String = Clock.System.now().toString()) {
}
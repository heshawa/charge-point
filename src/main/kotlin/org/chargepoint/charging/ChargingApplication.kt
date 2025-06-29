package org.chargepoint.charging

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ChargingApplication

fun main(args: Array<String>) {
	runApplication<ChargingApplication>(*args)
}

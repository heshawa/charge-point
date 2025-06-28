package org.chargepoint.charging

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan
@SpringBootApplication
class ChargingApplication

fun main(args: Array<String>) {
	runApplication<ChargingApplication>(*args)
}

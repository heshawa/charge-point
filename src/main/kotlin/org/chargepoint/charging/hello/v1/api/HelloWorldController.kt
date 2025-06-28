package org.chargepoint.charging.hello.v1.api

import jakarta.validation.constraints.Pattern
import org.chargepoint.charging.hello.v1.dto.HelloResponse
import org.chargepoint.charging.hello.v1.service.HelloService
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1/hello")
@RestController
@Validated
class HelloWorldController(
    private val helloService: HelloService
) {
    
    @GetMapping("/{userName}")
    fun sayHello(
        @PathVariable
        @Pattern(regexp = "[A-Za-z\\s]{1,50}$", message = "Invalid name provided")
        userName:String) : ResponseEntity<HelloResponse>{
        return ResponseEntity.ok(helloService.getHelloWorldMessage(userName))
    }
}
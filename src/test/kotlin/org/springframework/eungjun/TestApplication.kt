package org.springframework.eungjun

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@SpringBootApplication
class TestApplication {

    class MyService {
        fun hello(name: String) = "Hello, $name"
    }

    @Bean
    fun myService(): MyService = MyService()

    @Controller
    class TestController(
        private val myService: MyService
    ) {
        @GetMapping("/")
        fun welcome(name: String): ResponseEntity<String> = ResponseEntity.ok().body(myService.hello(name))
    }
}

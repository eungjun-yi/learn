package org.springframework

import io.restassured.RestAssured
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.Matchers
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@SpringBootTest(
    classes = [WebTest.TestApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class WebTest(
    @LocalServerPort private val serverPort: Int
) {

    @Test
    fun `인증이 필요없는 리소스에 접근하는 경우`() {
        RestAssured.port = serverPort

        When {
            get("/?name=foo")
        }.Then {
            statusCode(200)
            body(Matchers.equalTo("hello, foo"))
        }
    }

    @SpringBootApplication
    class TestApplication {

        class MyService {
            fun hello(name: String) = "Hello, $name"
        }

        @Configuration
        class Beans {
            @Bean
            fun myService(): MyService = MyService()
        }

        @Controller
        class TestController(
            private val myService: MyService
        ) {

            @GetMapping("/")
            fun welcome(name: String): ResponseEntity<String> = ResponseEntity.ok().body(myService.hello(name))
        }
    }
}

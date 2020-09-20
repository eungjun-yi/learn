package org.springframework.eungjun // component scan을 피하기 위해 이름을 아무거나로 바꿈

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.restassured.RestAssured
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.eungjun.TestApplication.*

@SpringBootTest(
    classes = [TestApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class WebTest3(
    @LocalServerPort private val serverPort: Int
) {
    init {
        RestAssured.port = serverPort
    }

    @MockkBean private lateinit var myService: MyService

    @Test
    fun `인증이 필요없는 리소스에 접근하는 경우`() {
        every { myService.hello("foo") } returns "Hello, foo"

        When {
            get("/?name=foo")
        }.Then {
            statusCode(200)
            body(Matchers.equalTo("Hello, foo"))
        }
    }

}

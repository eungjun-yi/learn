package org.springframework

import im.toss.test.equalsTo
import org.junit.jupiter.api.Test
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

class UriComponentBuilderTest {

    @Test
    fun test() {
        val originUri = URI.create("https://simg.wooribank.com/img/wsm/ccd/ccdmall/20191113151027_카드의정석 COOKIE CHECK_스무살우리245166.png")

        val uri = UriComponentsBuilder.fromUriString(originUri.host)
            .path(originUri.path)
            .encode()
            .build()

        val uri2 = UriComponentsBuilder.fromUriString("https://simg.wooribank.com/img/wsm/ccd/ccdmall/20191113151027_%EC%B9%B4%EB%93%9C%EC%9D%98%EC%A0%95%EC%84%9D%20COOKIE%20CHECK_%EC%8A%A4%EB%AC%B4%EC%82%B4%EC%9A%B0%EB%A6%AC245166.png")
            .encode()
            .build()

        uri.equalsTo(uri2)
    }
}

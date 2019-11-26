package com.npcode.learning.kotlin

import com.npcode.learning.kotlin.Client.Asset
import im.toss.test.equalsTo
import org.junit.jupiter.api.Test

class Client{
    fun handle(asset: Asset): Int {
        return asset.amount()
    }

    // 인터페이스를 정의하는 것은 클라이언트
    interface Asset {
        fun amount(): Int
    }
}

// 운이 좋다면 구현의 메서드 이름이 클라이언트가 요구하는 인터페이스와 "우연히" 일치할 수 있다.
class MyMoney: AmountHolder {
    override fun amount() = 1
}

interface AmountHolder {
    fun amount(): Int
}

// 운이 좋은 경우
private fun adopt(myMoney: MyMoney) = object : Asset, AmountHolder by myMoney {}

// 그러나 항상 운이 좋은 것은 아니다.
class MyInvest: Valuable {
    override fun value() = 2
}

interface Valuable {
    fun value(): Int
}

// 그러면 변환을 해 주어야한다
private fun adopt(valuable: Valuable): Asset = object: Asset { override fun amount() = valuable.value() }

// 변환은 누가 하는가? client를 사용하는 쪽에서 해야한다. application이 될 것이다. domain은 단순함을 유지한다.

class IspTest {
    @Test
    fun test() {
        // 운이 좋아서 메서드 signature가 일치한다면 어댑터는 필요없다. (그러나 운이 좋은 것 치고는 손이 간다)
        Client().handle(adopt(MyMoney())).equalsTo(1)
        // 운이 나빠서 메서드 signature가 불일치한다면 어댑터가 필요하다.
        Client().handle(adopt(MyInvest())).equalsTo(2)
    }
}

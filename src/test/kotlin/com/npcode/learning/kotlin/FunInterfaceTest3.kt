import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class FunInterfaceTest2 {
    private val user = User(money = 100)

    @Test
    fun testFunctionType() {
        val isRich: User.() -> Boolean = { money > 10 }
        user.isRich() shouldBe true
    }

    @Test
    fun testTypeAlias() {
        val isRich: UserIsRich1 = { money > 10 }
        user.isRich() shouldBe true
    }

    @Test
    fun testFunctionalInterface() {
        UserIsRich2 { money > 10 }.run {
            user.isRich() shouldBe true
        }
    }

    @Test
    fun testFunctionalInterface2() {
        val userIsRich = UserIsRich3 { it.money > 10 }
        val isRich: User.() -> Boolean = { userIsRich(this) }
        UserIsRich2 { money > 10 }.run {
            user.isRich() shouldBe true
        }
    }
}

data class User(val money: Int = 0)

typealias UserIsRich1 = User.() -> Boolean

fun interface UserIsRich2 {
    fun User.isRich(): Boolean
}

fun interface UserIsRich3 {
    operator fun invoke(user: User): Boolean
}

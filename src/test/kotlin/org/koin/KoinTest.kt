package org.koin

import org.junit.jupiter.api.Test
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.inject
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.check.checkModules
import org.koin.test.get

// KoinTest 상속하지 않고 하기
class SimpleTest0 {
    @Test
    fun testProdModule() {
        // startKoin을 매번 하는 것이 번거롭다
        startKoin { modules(prodModule) }
        MyServices().myService1.foo()
    }
}

class SimpleTest1 {
    @Test
    fun testProdModule() {
        MyFactory.myService1().foo()
    }

    @Test
    fun testProdModule2() {
        MyFactory2().myService1.foo()
    }
}

// KoinTest를 상속하고 하기
class SimpleTest: KoinTest {

    @Test
    fun testProdModule() {
        startKoin { modules(prodModule) }
        get<MyService>().foo()
    }

    @Test
    fun testTestModule() {
        startKoin { modules(testModule) }
        get<MyService>().foo()
    }

    @Test
    fun testCircularDependency() {
        startKoin { modules(moduleHavingCircularDependency) }.checkModules() // stack overflow
    }

    @Test
    fun testMissingDependency() {
        startKoin { modules(moduleHavingMissingDependency) }.checkModules() // InstantCreationException
    }
}

class MyService(private val subService: MySubService) {
    fun foo() = System.out.println(subService.bar())
}

interface MySubService {
    fun bar(): Int
}

class ProdMySubService: MySubService {
    override fun bar(): Int = 1
}

class FakeMySubService: MySubService {
    override fun bar(): Int = 0
}

val prodModule = module {
    single { MyService(get()) }
    single { ProdMySubService() } bind MySubService::class
}

val testModule = module {
    single { MyService(get()) }
    single { FakeMySubService() } bind MySubService::class
}

class MyBadService1(private val myBadService2: MyBadService2)
class MyBadService2(private val myBadService1: MyBadService1)

val moduleHavingCircularDependency = module {
    single { MyBadService1(get()) }
    single { MyBadService2(get()) }
}

val moduleHavingMissingDependency = module {
    single { MyBadService1(get()) }
}

class MyServices: KoinComponent {
    val myService1 by inject<MyService>()
}

class MyFactory {
    companion object {
        fun myService1(): MyService {
            startKoin { modules(prodModule) }
            return MyServices().myService1
        }
    }
}

class MyFactory2: KoinComponent {
    init {
        startKoin {
            modules(
                module {
                    single { MyService(get()) }
                    single { ProdMySubService() } bind MySubService::class
                }
            )
        }
    }

    val myService1 by inject<MyService>()
}

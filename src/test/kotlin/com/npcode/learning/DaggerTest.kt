package com.npcode.learning

import dagger.Module
import dagger.Provides
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import javax.inject.Inject

class DaggerTest(
    @Inject val coffeeMaker: CoffeeMaker
) {

    @Test
    fun makeCoffeeMaker() {
        assertThat(coffeeMaker.heater).isInstanceOf(ElectricHeater::class.java)
    }
}

class CoffeeMaker(
    @Inject val heater: Heater
)

@Module
internal object DripCoffeeModule {
    @Provides
    fun provideHeater(): Heater {
        return ElectricHeater()
    }
}

interface Heater

class ElectricHeater: Heater

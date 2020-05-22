package com.npcode.learning.reactor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.blockhound.BlockHound
import reactor.blockhound.BlockingOperationError
import reactor.core.publisher.Mono
import java.time.Duration

private val logger = object : reactor.util.Logger, Logger by LoggerFactory.getLogger("mytest") {}

class BlockingIoTest {

    @Test
    fun `logging is not considered as a blocking operation`() {
        BlockHound.install()

        Mono.delay(Duration.ofSeconds(1))
            .doOnNext {
                try {
                    logger.info("okok")
                } catch (e: InterruptedException) {
                    throw RuntimeException(e)
                }
            }
            .block()
    }

    @Test
    fun `reactor detects reactor block method even if BlockHound is not installed`() {
        Mono.delay(Duration.ofSeconds(1))
            .doOnNext {
                try {
                    Mono.delay(Duration.ofMillis(10)).block()
                } catch (e: InterruptedException) {
                    throw RuntimeException(e)
                }
            }
            .block()
    }

    @Test
    fun `Throws BlockingOperationError if blocking operation is called in coroutine scope`() {
        BlockHound.install()

        assertThrows<BlockingOperationError> {
            runBlocking {
                withContext(Dispatchers.Default) {
                    Thread.sleep(10)
                }
            }
        }
    }
}

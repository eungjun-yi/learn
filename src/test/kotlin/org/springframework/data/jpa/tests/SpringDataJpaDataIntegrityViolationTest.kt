package org.springframework.data.jpa.tests

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.Example
import org.springframework.data.jpa.FooJpaRepository
import org.springframework.data.jpa.Person
import org.springframework.data.jpa.domain.Specification
import javax.persistence.criteria.Path
import kotlin.reflect.KProperty1

@DataJpaTest
class SpringDataJpaDataIntegrityViolationTest {

    @Autowired
    private lateinit var fooJpaRepository: FooJpaRepository

    private val person1 = Person(name = "a")
    private val person2 = Person(name = "b")

    @BeforeEach
    fun beforeEach() {
        fooJpaRepository.save(person1)
        fooJpaRepository.save(person2)
    }

    @AfterEach
    fun afterEach() {
        fooJpaRepository.deleteAll()
    }

    @Test
    fun test() {
        // 이런다고
        fooJpaRepository.save(Person(id = person1.id, name = "c"))
        fooJpaRepository.findAll().size shouldBe 2
    }
}

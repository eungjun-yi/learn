package org.springframework.data.jpa

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.Example
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.criteria.Path
import kotlin.reflect.KProperty1

@DataJpaTest
class SpringDataJpaTest {

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
    fun testExample() {
        val example = Example.of(person1)
        fooJpaRepository.findAll(example) shouldBe listOf(person1)
    }

    @Test
    fun testPredicate() {
        val spec1 = Specification<Person> { root, _, criteriaBuilder ->
            criteriaBuilder.equal(root.get(Person::name), "b")
        }
        fooJpaRepository.findAll(spec1) shouldBe listOf(person2)
    }
}

fun <T, R> Path<T>.get(property: KProperty1<T, R>): Path<R> = get(property.name)

@SpringBootApplication
class TestApplication

@Entity
class Person(
    @Id
    @GeneratedValue
    val id: Long? = null,
    val name: String,
)

interface FooJpaRepository : JpaRepository<Person, Long>, JpaSpecificationExecutor<Person>

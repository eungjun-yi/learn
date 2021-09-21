package org.springframework.data.jpa.tests

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.jpa.FooJpaRepository
import org.springframework.data.jpa.Person

@DataJpaTest
class DbConnectionTest {

    @Autowired
    private lateinit var fooJpaRepository: FooJpaRepository

    @Test
    fun test() {
        fooJpaRepository.save(Person(id = 1, name = "foo"))
        fooJpaRepository.save(Person(id = 2, name = "bar"))

        // 하나의 DB connection에서 query가 병렬로 실행될 수 있는지
        // 안될 것 같다. 대부분 안된다고 함.
        // 어차피 이 테스트는 pooling을 안하는 것 같다. 매번 커넥션이 생성됨
        runBlocking(Dispatchers.IO) {
            repeat(100) {
                launch {
                    println("begin1 $this")
                    fooJpaRepository.findById(1)
                    println("end1 $this")
                }
                launch {
                    println("begin2 $this")
                    fooJpaRepository.findByName("bar")
                    println("end2 $this")
                }
            }
        }
    }
}

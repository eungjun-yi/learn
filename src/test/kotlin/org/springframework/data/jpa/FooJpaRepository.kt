package org.springframework.data.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface FooJpaRepository : JpaRepository<Person, Long>, JpaSpecificationExecutor<Person>, FooRepositoryCustom {
    fun findByName(name: String): Person?
}

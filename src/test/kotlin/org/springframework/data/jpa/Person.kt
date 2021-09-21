package org.springframework.data.jpa

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Person(
    @Id
    @GeneratedValue
    val id: Long? = null,
    val name: String,
)

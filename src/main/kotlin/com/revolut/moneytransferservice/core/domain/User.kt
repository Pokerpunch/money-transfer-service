package com.revolut.moneytransferservice.core.domain

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.SEQUENCE
import javax.persistence.Id
import javax.persistence.SequenceGenerator
import javax.persistence.Table
import javax.persistence.Version

private const val USER_SEQUENCE_GENERATOR = "user_generator"

@Entity
@Table(name = "users")
data class User(

    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = USER_SEQUENCE_GENERATOR)
    @SequenceGenerator(name = USER_SEQUENCE_GENERATOR, sequenceName = "user_sequence", allocationSize = 1)
    val id: Long = -1,

    @Version
    @Column(nullable = false)
    val version: Long = 0,

    @Column(nullable = false)
    val firstName: String = "",

    @Column(nullable = false)
    val lastName: String = ""
) {
    @Column(nullable = false, updatable = false)
    @field: CreationTimestamp
    val dateCreated = LocalDateTime.now()
}
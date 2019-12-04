package com.revolut.moneytransferservice.core.domain

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.SequenceGenerator
import javax.persistence.Table
import javax.persistence.Version

private const val ACCOUNT_SEQUENCE_GENERATOR = "user_generator"

@Entity
@Table(name = "accounts")
data class Account(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ACCOUNT_SEQUENCE_GENERATOR)
    @SequenceGenerator(name = ACCOUNT_SEQUENCE_GENERATOR, sequenceName = "account_sequence", allocationSize = 1)
    val id: Long = -1,

    @Version
    @Column(nullable = false)
    val version: Long = 0,

    @Column(nullable = false)
    val accountOwnerId: Long = -1
) {
    @Column(nullable = false, updatable = true)
    var balanceInMinor: Long = 0

    @Column(nullable = false, updatable = false)
    @field: CreationTimestamp
    val dateCreated = LocalDateTime.now()

    @Column(nullable = false, updatable = true)
    @field: UpdateTimestamp
    val dateUpdated = LocalDateTime.now()
}
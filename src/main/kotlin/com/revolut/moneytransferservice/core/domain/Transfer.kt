package com.revolut.moneytransferservice.core.domain

import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.SequenceGenerator
import javax.persistence.Table

private const val TRANSFER_SEQUENCE_GENERATOR = "transfer_generator"

@Entity
@Table(name = "transfers")
data class Transfer(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TRANSFER_SEQUENCE_GENERATOR)
    @SequenceGenerator(name = TRANSFER_SEQUENCE_GENERATOR, sequenceName = "transfer_sequence", allocationSize = 1)
    val id: Long = -1,

    @Column(nullable = false)
    val originAccountId: Long = -1,

    @Column(nullable = false)
    val destinationAccountId: Long = -1,

    @Column(nullable = false)
    val amountInMinor: Long = -1
) {
    @Column(nullable = false, updatable = false)
    @field: CreationTimestamp
    val dateCreated = LocalDateTime.now()
}
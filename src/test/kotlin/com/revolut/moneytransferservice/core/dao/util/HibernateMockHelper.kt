package com.revolut.moneytransferservice.core.dao.util

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.Transaction
import org.hibernate.query.Query
import javax.persistence.LockModeType
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery

object HibernateMockHelper {

    fun mockSession(sessionFactory: SessionFactory): Session {
        val session: Session = mock {}
        whenever(sessionFactory.currentSession).thenReturn(session)

        return session
    }

    fun mockTransaction(session: Session): Transaction {
        val transaction: Transaction = mock {}
        whenever(transaction.isActive).thenReturn(false).thenReturn(true)

        whenever(session.beginTransaction()).thenReturn(transaction)
        whenever(session.transaction).thenReturn(transaction)

        return transaction
    }

    inline fun <reified T> mockFindById(session: Session, id: Long, returnValue: T?) {
        whenever(session.find(T::class.java, id)).thenReturn(returnValue)
    }

    inline fun <reified T> mockFindByIdWithLock(session: Session, id: Long, lockModeType: LockModeType, returnValue: T?) {
        whenever(session.find(T::class.java, id, lockModeType)).thenReturn(returnValue)
    }

    inline fun <reified T> mockFindAll(session: Session, returnValues: List<T>) {
        val criteriaBuilder: CriteriaBuilder = mock {}
        whenever(session.criteriaBuilder).thenReturn(criteriaBuilder)

        val criteriaQuery: CriteriaQuery<T> = mock {}
        whenever(criteriaBuilder.createQuery(T::class.java)).thenReturn(criteriaQuery)

        val query: Query<T> = mock {}
        whenever(session.createQuery(criteriaQuery)).thenReturn(query)
        whenever(query.resultList).thenReturn(returnValues)
    }
 }
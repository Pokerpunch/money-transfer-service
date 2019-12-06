package com.revolut.moneytransferservice.core.dao

import org.hibernate.Session
import org.hibernate.SessionFactory

abstract class HibernateDAO<T, in ID : java.io.Serializable>(
    private val entityType: Class<T>,
    private val sessionFactory: SessionFactory
) : DAO<T, ID> {

    override fun findById(id: ID): T? =
        runInTransaction {
            getCurrentSession().find(entityType, id)
        }

    override fun fetchAll(): List<T> =
        runInTransaction {
            val session = getCurrentSession()
            val criteria = session.criteriaBuilder
                .createQuery(entityType).apply { from(entityType) }

            session.createQuery(criteria).resultList
        }

    override fun save(entity: T): T =
        runInTransaction {
            val id: ID = getCurrentSession().save(entity) as ID
            findById(id)!!
        }

    protected fun getCurrentSession(): Session = sessionFactory.currentSession

    protected fun <T> runInTransaction(body: () -> T): T =
        getCurrentSession().let { session ->
            if (session.transaction.isActive)
                runInActiveTransaction(session, body)
            else
                runInNewTransaction(body)
        }

    private fun <T> runInActiveTransaction(session: Session, body: () -> T): T =
        also {
            if (!session.transaction.isActive) {
                throw IllegalStateException("Transaction is not active!")
            }
        }.run {
            body()
        }

    private fun <T> runInNewTransaction(body: () -> T): T =
        getCurrentSession().use { session ->
            runCatching {
                also {
                    session.beginTransaction()
                }.run {
                    body()
                }.also {
                    session.flush()
                    session.transaction.commit()
                }
            }.onFailure {
                session.transaction.rollback()
            }.getOrThrow()
        }
}
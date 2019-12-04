package com.revolut.moneytransferservice.core.dao

interface DAO<T, in ID : java.io.Serializable> {

    fun findById(id: ID): T?

    fun fetchAll(): List<T>

    fun save(entity: T)
}
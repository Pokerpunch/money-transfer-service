package com.revolut.moneytransferservice.core.dao

import com.revolut.moneytransferservice.core.domain.User
import org.hibernate.SessionFactory

class UserDAO(
    sessionFactory: SessionFactory
) : HibernateDAO<User, Long>(User::class.java, sessionFactory)
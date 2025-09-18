package com.rodionov.local.repository.user

import com.rodionov.domain.models.user.User
import com.rodionov.domain.repository.user.UserRepository
import com.rodionov.local.dao.UserDao

class UserRepositoryImpl(
    private val userDao: UserDao
): UserRepository {

    override suspend fun saveUser(user: User): Result<Any> {
        return userDao.insert(user.)
    }

    override suspend fun retrieveUser(): Result<User> {
        TODO("Not yet implemented")
    }
}
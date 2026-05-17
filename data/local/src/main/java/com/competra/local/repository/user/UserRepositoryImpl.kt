package com.competra.local.repository.user

import com.competra.domain.models.user.User
import com.competra.domain.repository.user.UserRepository
import com.competra.local.dao.UserDao
import com.competra.local.mappers.toDomain
import com.competra.local.mappers.toEntity

class UserRepositoryImpl(
    private val userDao: UserDao
): UserRepository {

    override suspend fun saveUser(user: User): Result<Any> {
        return Result.success(userDao.insert(user.toEntity()))
    }

    override suspend fun retrieveUser(): Result<User> {
        return try {
            val userEntity = userDao.getUser() ?: throw Exception("User not authorized")
            Result.success(userEntity.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isAuthorized(): Boolean {
        val user = userDao.getUser()
        return user != null
    }

    override suspend fun clearUser() {
        userDao.clearAll()
    }
}
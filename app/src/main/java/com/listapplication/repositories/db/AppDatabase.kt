package com.listapplication.repositories.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.listapplication.repositories.db.dao.RepoDao
import com.listapplication.repositories.models.Repository
import com.listapplication.utils.ConstantUtil.Companion.DB_VERSION

/**
 * Abstract database class to create SQLite DB.
 */
@Database(entities = [Repository::class], version = DB_VERSION)
abstract class AppDatabase : RoomDatabase() {
    abstract fun repoDao(): RepoDao
}
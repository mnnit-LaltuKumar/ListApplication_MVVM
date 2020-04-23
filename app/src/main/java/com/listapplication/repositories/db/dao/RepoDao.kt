package com.listapplication.repositories.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SupportSQLiteQuery
import com.listapplication.repositories.models.Repository
import com.listapplication.utils.ConstantUtil.Companion.TABLE_REPO
import io.reactivex.Maybe

@Dao
interface RepoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(repoList: List<Repository>)

    @Query("DELETE FROM $TABLE_REPO")
    fun deleteAllRepos()

    @Query("SELECT * FROM $TABLE_REPO")
    fun getAllRepos(): Maybe<List<Repository>>

    @RawQuery
    fun rawQuery(query: SupportSQLiteQuery): Maybe<List<Repository>>

    @Transaction
    fun refreshRepoData(repoList: List<Repository>) {
        deleteAllRepos()
        insertAll(repoList)
    }
}
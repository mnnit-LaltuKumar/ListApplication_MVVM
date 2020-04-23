package com.listapplication.repositories.db.datasource

import com.listapplication.CustomLogs
import com.listapplication.repositories.db.dao.RepoDao
import com.listapplication.repositories.generics.DataSource
import com.listapplication.repositories.models.Repository
import com.listapplication.utils.ConstantUtil.Companion.TABLE_REPO
import com.listapplication.utils.DBUtil
import io.reactivex.Completable
import io.reactivex.Flowable

/**
 * Database source to implement DataSource interface to provide abstraction from outer classes.
 * Implement all the datasource interface methods here and provide db connectivity.
 */
class RepoDataSource(
    private val mRepoDao: RepoDao,
    private val mCustomLogs: CustomLogs
): DataSource<Repository> {

    override fun getAll(): Flowable<List<Repository>> {
        mCustomLogs("Getting repositories from db")
        return mRepoDao.getAllRepos().toFlowable()
    }

    override fun getAll(query: DataSource.Query<Repository>): Flowable<List<Repository>> {
        mCustomLogs("Getting Filtered Results")
        return mRepoDao.rawQuery(DBUtil.sqlWhere(TABLE_REPO, query.params)).toFlowable()
    }

    override fun getData(data: Repository): Flowable<Repository> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveAll(list: List<Repository>): Flowable<List<Repository>> {
        return Completable.fromCallable {
            mCustomLogs("Inserting repositories to db")
            mRepoDao.refreshRepoData(list)
        }.andThen(Flowable.just(list))
    }

    override fun deleteAll(): Completable {
        return Completable.fromCallable {
            mCustomLogs("Deleting repositories from db")
            mRepoDao.deleteAllRepos()
        }
    }

}
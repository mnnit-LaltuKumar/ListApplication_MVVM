package com.listapplication.repositories.network.datasource

import com.listapplication.CustomLogs
import com.listapplication.repositories.generics.DataSource
import com.listapplication.repositories.models.Repository
import com.listapplication.repositories.network.NetworkInterface
import com.listapplication.utils.PreferencesUtil
import io.reactivex.Completable
import io.reactivex.Flowable
import java.util.Calendar

/**
 * Network source to implement DataSource interface to provide abstraction from outer classes.
 * Implement all the datasource interface methods here and provide network connectivity.
 */
class RepoApiSource(
    private val mRestAPI: NetworkInterface,
    private val mPreferencesUtil: PreferencesUtil,
    private val mCustomLogs: CustomLogs
) : DataSource<Repository> {

    override fun getAll(): Flowable<List<Repository>> {
        mCustomLogs("RepoApiSource :: getAll() Triggered")
        return mRestAPI
            .fetchTrendingRepositories()
            .doOnSuccess { calculateResponseTimoutAndSave() }
            .toFlowable()
    }

    override fun getAll(query: DataSource.Query<Repository>): Flowable<List<Repository>> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getData(data: Repository): Flowable<Repository> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun saveAll(list: List<Repository>): Flowable<List<Repository>> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteAll(): Completable {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    private fun calculateResponseTimoutAndSave() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR, 2)
        mCustomLogs("RepoAPISource :: Saving response timeout ${calendar.time}")
        mPreferencesUtil.saveResponseTimeout(calendar.timeInMillis)
    }
}
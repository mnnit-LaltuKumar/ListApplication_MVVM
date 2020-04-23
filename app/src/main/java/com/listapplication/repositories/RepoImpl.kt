package com.listapplication.repositories

import android.content.Context
import com.listapplication.CustomLogs
import com.listapplication.repositories.generics.DataSource
import com.listapplication.utils.NetworkUtil
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers

/**
 * Generic repository implementation.
 * Here we bind db and network data together and share the result across.
 * Uses koin module to generate singleton reference of T type class.
 */
class RepoImpl<T : Any>(
    private val mContext: Context,
    private val mRestApi: DataSource<T>,
    private val mDB: DataSource<T>,
    private val mCustomLogs: CustomLogs
) : DataSource<T> {

    override fun getAll(): Flowable<List<T>> {
        return Flowable.concatArrayEager(
            mDB.getAll().subscribeOn(Schedulers.io()),
            Flowable.defer {
                if (NetworkUtil.isNetworkAvailable(mContext)) {
                    mRestApi.getAll()
                        .subscribeOn(Schedulers.io())
                        .flatMap { list ->
                            mDB.saveAll(list)
                        }
                } else {
                    Flowable.empty()
                }
            }.subscribeOn(Schedulers.io())
        )
    }

    override fun getAll(query: DataSource.Query<T>): Flowable<List<T>> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getData(data: T): Flowable<T> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun saveAll(list: List<T>): Flowable<List<T>> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteAll(): Completable {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }
}
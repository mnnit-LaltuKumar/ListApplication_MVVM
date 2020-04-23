package com.listapplication.repositories.generics

import io.reactivex.Completable
import io.reactivex.Flowable

/**
 * Generic datasource interface which follows repository pattern for connecting db and network.
 * It performs common operations and bind db and network data together.
 */
interface DataSource<T : Any> {

    fun getData(data: T): Flowable<T>

    fun getAll(): Flowable<List<T>>

    fun getAll(query: Query<T>): Flowable<List<T>>

    fun saveAll(list: List<T>): Flowable<List<T>>

    fun deleteAll(): Completable

    fun rawQuery(): Query<T> {
        return Query(this)
    }

    class Query<T : Any>(private val dataSource: DataSource<T>) {

        val params: MutableMap<String, String> = mutableMapOf()

        fun has(key: String): Boolean {
            return params[key] != null
        }

        fun get(key: String): String? {
            return params[key]
        }

        fun where(key: String, value: String): Query<T> {
            params[key] = value
            return this
        }

        fun findAll(): Flowable<List<T>> {
            return dataSource.getAll(this)
        }
    }
}
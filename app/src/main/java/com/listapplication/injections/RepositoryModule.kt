package com.listapplication.injections

import androidx.room.Room
import com.listapplication.repositories.RepoImpl
import com.listapplication.repositories.db.AppDatabase
import com.listapplication.repositories.db.datasource.RepoDataSource
import com.listapplication.repositories.generics.DataSource
import com.listapplication.repositories.models.Repository
import com.listapplication.repositories.network.datasource.RepoApiSource
import com.listapplication.utils.ConstantUtil.Companion.DB_NAME
import org.koin.dsl.module.module

val mRepositoryModule = module {
    single { Room.databaseBuilder(get(), AppDatabase::class.java, DB_NAME).build() }
    single { get<AppDatabase>().repoDao() }
    single<DataSource<Repository>>(name = "repoDB") { RepoDataSource(get(), get()) }
    single<DataSource<Repository>>(name = "repoAPI") { RepoApiSource(get(), get(), get()) }

    single<DataSource<Repository>>(name = "repoDataImpl") { RepoImpl(get(), get("repoAPI"), get("repoDB"), get()) }
}
package com.listapplication.repositories.network

import com.listapplication.repositories.models.Repository
import com.listapplication.repositories.models.RepositoryParent
import io.reactivex.Single
import retrofit2.http.GET

interface NetworkInterface {

    @GET("/repositories")
    fun fetchTrendingRepositories(): Single<List<Repository>>

    fun fetchDataFromJson(): Single<RepositoryParent>
}
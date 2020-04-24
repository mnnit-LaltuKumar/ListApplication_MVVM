package com.listapplication.repositories.network.datasource

import com.listapplication.repositories.models.Repository
import com.listapplication.repositories.network.NetworkInterface
import com.listapplication.utils.PreferencesUtil
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyLong
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class RepoApiSourceTest {

    private lateinit var mMockRestAPI: NetworkInterface

    private val mBaseRepo = Repository(
        1,
        "Test",
        "Test-Name",
        "",
        "Test-Desc",
        "Kotlin",
        "",
        200,
        100
    )

    @Before
    fun setup() {
        mMockRestAPI = mock(NetworkInterface::class.java)
    }

    @Test
    fun evaluateOutput_restResponseSuccessForFetchRepositories() {
        val mockPreferencesUtil = mock(PreferencesUtil::class.java)
        `when`(mMockRestAPI.fetchTrendingRepositories()).thenReturn(Single.just(listOf(mBaseRepo)))
        val repoApiSource = RepoApiSource(mMockRestAPI, mockPreferencesUtil) { 0 }
        repoApiSource
            .getAll()
            .test()
            .await()
            .assertComplete()
            .assertValue {
                it.isNotEmpty() && it[0].name == mBaseRepo.name
            }
    }

    @Test
    fun evaluateOutput_storeExpiryTimeOnSuccessForFetchRepositories() {
        val mockPreferencesUtil = mock(PreferencesUtil::class.java)
        `when`(mMockRestAPI.fetchTrendingRepositories()).thenReturn(Single.just(listOf(mBaseRepo)))
        val repoApiSource = RepoApiSource(mMockRestAPI, mockPreferencesUtil) { 0 }
        repoApiSource
            .getAll()
            .test()
            .await()
            .assertComplete()

        verify(mockPreferencesUtil, times(1)).saveResponseTimeout(anyLong())
    }

    @Test
    fun evaluateOutput_restResponseErrorForFetchRepositories() {
        val mockPreferencesUtil = mock(PreferencesUtil::class.java)
        `when`(mMockRestAPI.fetchTrendingRepositories()).thenReturn(Single.error(Exception("Server Error")))
        val repoApiSource = RepoApiSource(mMockRestAPI, mockPreferencesUtil) { 0 }
        repoApiSource
            .getAll()
            .test()
            .await()
            .assertNotComplete()
            .assertErrorMessage("Server Error")
    }
}
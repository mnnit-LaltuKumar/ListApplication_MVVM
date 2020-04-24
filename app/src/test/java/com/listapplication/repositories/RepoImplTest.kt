package com.listapplication.repositories

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.listapplication.repositories.generics.DataSource
import com.listapplication.repositories.models.Repository
import io.reactivex.Completable
import io.reactivex.Flowable
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyList
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class RepoImplTest {

    private lateinit var mMockRestDataSource: DataSource<Repository>

    private lateinit var mMockDBDataSource: DataSource<Repository>

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
    @Suppress("UNCHECKED_CAST")
    fun setup() {
        mMockRestDataSource = mock(DataSource::class.java) as DataSource<Repository>
        mMockDBDataSource = mock(DataSource::class.java) as DataSource<Repository>
    }

    @Test
    fun evaluateResult_sameResponseDbSuccessAndRestResponseSuccess() {
        val mockContext = mock(Context::class.java)
        val mockConnectivityManager = mock(ConnectivityManager::class.java)
        val mockNetworkInfo = mock(NetworkInfo::class.java)
        `when`(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockConnectivityManager)
        `when`(mockConnectivityManager.activeNetworkInfo).thenReturn(mockNetworkInfo)
        `when`(mockNetworkInfo.isConnected).thenReturn(true)
        `when`(mMockDBDataSource.getAll()).thenReturn(Flowable.just(listOf(mBaseRepo)))
        `when`(mMockRestDataSource.getAll()).thenReturn(Flowable.just(listOf(mBaseRepo)))
        `when`(mMockDBDataSource.deleteAll()).thenReturn(Completable.complete())
        `when`(mMockDBDataSource.saveAll(anyList())).thenReturn(Flowable.just(listOf(mBaseRepo)))
        val repoImpl = RepoImpl(mockContext, mMockRestDataSource, mMockDBDataSource) { 0 }
        repoImpl.getAll()
            .test()
            .await()
            .assertComplete()
            .assertNoErrors()
            .assertValueCount(2)
    }

    @Test
    fun evaluateResult_dbSuccessAndRestResponseServerError() {
        val mockContext = mock(Context::class.java)
        val mockConnectivityManager = mock(ConnectivityManager::class.java)
        val mockNetworkInfo = mock(NetworkInfo::class.java)
        `when`(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockConnectivityManager)
        `when`(mockConnectivityManager.activeNetworkInfo).thenReturn(mockNetworkInfo)
        `when`(mockNetworkInfo.isConnected).thenReturn(true)
        `when`(mMockDBDataSource.getAll()).thenReturn(Flowable.just(listOf(mBaseRepo)))
        `when`(mMockRestDataSource.getAll()).thenReturn(Flowable.error(Exception("Server Error")))
        val repoImpl = RepoImpl(mockContext, mMockRestDataSource, mMockDBDataSource) { 0 }
        repoImpl.getAll()
            .test()
            .await()
            .assertNotComplete()
            .assertErrorMessage("Server Error")
    }

    @Test
    fun evaluateResult_dbErrorAndRestResponseServerSuccess() {
        val mockContext = mock(Context::class.java)
        val mockConnectivityManager = mock(ConnectivityManager::class.java)
        val mockNetworkInfo = mock(NetworkInfo::class.java)
        `when`(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockConnectivityManager)
        `when`(mockConnectivityManager.activeNetworkInfo).thenReturn(mockNetworkInfo)
        `when`(mockNetworkInfo.isConnected).thenReturn(true)
        `when`(mMockDBDataSource.getAll()).thenReturn(Flowable.error(Exception("DB Error")))
        `when`(mMockRestDataSource.getAll()).thenReturn(Flowable.just(listOf(mBaseRepo)))
        `when`(mMockDBDataSource.deleteAll()).thenReturn(Completable.complete())
        `when`(mMockDBDataSource.saveAll(anyList())).thenReturn(Flowable.just(listOf(mBaseRepo)))
        val repoImpl = RepoImpl(mockContext, mMockRestDataSource, mMockDBDataSource) { 0 }
        repoImpl.getAll()
            .test()
            .await()
            .assertNotComplete()
            .assertErrorMessage("DB Error")
    }

    @Test
    fun evaluateResult_dbSuccessAndRestResponseDbStoreError() {
        val mockContext = mock(Context::class.java)
        val mockConnectivityManager = mock(ConnectivityManager::class.java)
        val mockNetworkInfo = mock(NetworkInfo::class.java)
        `when`(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockConnectivityManager)
        `when`(mockConnectivityManager.activeNetworkInfo).thenReturn(mockNetworkInfo)
        `when`(mockNetworkInfo.isConnected).thenReturn(true)
        `when`(mMockDBDataSource.getAll()).thenReturn(Flowable.just(listOf(mBaseRepo)))
        `when`(mMockRestDataSource.getAll()).thenReturn(Flowable.just(listOf(mBaseRepo)))
        `when`(mMockDBDataSource.deleteAll()).thenReturn(Completable.complete())
        `when`(mMockDBDataSource.saveAll(anyList())).thenReturn(Flowable.error(Exception("DB Store Error")))
        val repoImpl = RepoImpl(mockContext, mMockRestDataSource, mMockDBDataSource) { 0 }
        repoImpl.getAll()
            .test()
            .await()
            .assertNotComplete()
            .assertErrorMessage("DB Store Error")
    }

    @Test
    fun evaluateResult_dbSuccessAndRestResponseConnectivityError() {
        val mockContext = mock(Context::class.java)
        val mockConnectivityManager = mock(ConnectivityManager::class.java)
        val mockNetworkInfo = mock(NetworkInfo::class.java)
        `when`(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockConnectivityManager)
        `when`(mockConnectivityManager.activeNetworkInfo).thenReturn(mockNetworkInfo)
        `when`(mockNetworkInfo.isConnected).thenReturn(false)
        `when`(mMockDBDataSource.getAll()).thenReturn(Flowable.just(listOf(mBaseRepo)))
        val repoImpl = RepoImpl(mockContext, mMockRestDataSource, mMockDBDataSource) { 0 }
        repoImpl.getAll()
            .test()
            .await()
            .assertComplete()
            .assertValueCount(1)
    }
}
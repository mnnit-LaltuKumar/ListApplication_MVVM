package com.listapplication.repositories.db.datasource

import com.listapplication.repositories.db.dao.RepoDao
import com.listapplication.repositories.models.Repository
import io.reactivex.Maybe
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class RepoDataSourceTest {

    private lateinit var mMockRepoDao: RepoDao

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
        mMockRepoDao = Mockito.mock(RepoDao::class.java)
    }

    @Test
    fun evaluateOutput_dbResponseSuccessForFetchRepositories() {
        Mockito.`when`(mMockRepoDao.getAllRepos()).thenReturn(Maybe.just(listOf(mBaseRepo)))
        val repoApiSource = RepoDataSource(mMockRepoDao) { 0 }
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
    fun evaluateOutput_dbResponseSuccessForFetchRepositoriesWhenListEmpty() {
        Mockito.`when`(mMockRepoDao.getAllRepos()).thenReturn(Maybe.just(listOf()))
        val repoApiSource = RepoDataSource(mMockRepoDao) { 0 }
        repoApiSource
            .getAll()
            .test()
            .await()
            .assertComplete()
            .assertValue {
                it.isEmpty()
            }
    }

    @Test
    fun evaluateOutput_dbResponseErrorForFetchRepositories() {
        Mockito.`when`(mMockRepoDao.getAllRepos()).thenReturn(Maybe.error(Exception("DB Error")))
        val repoApiSource = RepoDataSource(mMockRepoDao) { 0 }
        repoApiSource
            .getAll()
            .test()
            .await()
            .assertNotComplete()
            .assertErrorMessage("DB Error")
    }
}
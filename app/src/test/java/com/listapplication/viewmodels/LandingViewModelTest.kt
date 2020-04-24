package com.listapplication.viewmodels

import android.content.Context
import com.listapplication.repositories.generics.DataSource
import com.listapplication.repositories.models.Repository
import com.listapplication.utils.PreferencesUtil
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Flowable
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Calendar

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class LandingViewModelTest {

    private lateinit var mMockDataSource: DataSource<Repository>
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
        mMockDataSource = mock(DataSource::class.java) as DataSource<Repository>
        mMockDBDataSource = mock(DataSource::class.java) as DataSource<Repository>
    }

    @Test
    fun verifyInput_shouldTriggerPreferenceGetOnPreConditionCheckFlowable() {
        val mockContext = mock(Context::class.java)
        val mockPreferencesUtil = mock(PreferencesUtil::class.java)
        val viewModel = LandingViewModel(mockContext, mMockDataSource, mMockDBDataSource, mockPreferencesUtil) { 0 }
        viewModel.preConditionCheck(isForceFetch = false)
        verify(mockPreferencesUtil, times(1)).getResponseTimeout()
    }

    @Test
    fun verifyInput_shouldNotTriggerPreferenceGetWhenForceFetchTrueOnPreConditionCheckFlowable() {
        val mockContext = mock(Context::class.java)
        val mockPreferencesUtil = mock(PreferencesUtil::class.java)
        val viewModel = LandingViewModel(mockContext, mMockDataSource, mMockDBDataSource, mockPreferencesUtil) { 0 }
        viewModel.preConditionCheck(isForceFetch = true)
        verify(mockPreferencesUtil, times(0)).getResponseTimeout()
    }

    @Test
    fun verifyOutput_shouldTriggerDataSourceGetAllWhenForceRefresh() {
        val mockContext = mock(Context::class.java)
        val mockPreferencesUtil = mock(PreferencesUtil::class.java)
        val viewModel = LandingViewModel(mockContext, mMockDataSource, mMockDBDataSource, mockPreferencesUtil) { 0 }
        viewModel.preConditionCheck(isForceFetch = true)
        verify(mMockDataSource, times(1)).getAll()
        verify(mMockDBDataSource, times(0)).getAll()
    }

    @Test
    fun verifyOutput_shouldTriggerDataSourceGetAllWhenTimeNotStored() {
        val mockContext = mock(Context::class.java)
        val mockPreferencesUtil = mock(PreferencesUtil::class.java)
        val viewModel = LandingViewModel(mockContext, mMockDataSource, mMockDBDataSource, mockPreferencesUtil) { 0 }
        viewModel.preConditionCheck(isForceFetch = false)
        verify(mMockDataSource, times(1)).getAll()
        verify(mMockDBDataSource, times(0)).getAll()
    }

    @Test
    fun verifyOutput_shouldTriggerDataSourceGetAllWhenTimeExpired() {
        val mockContext = mock(Context::class.java)
        val mockPreferencesUtil = mock(PreferencesUtil::class.java)
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR, -1)
        `when`(mockPreferencesUtil.getResponseTimeout()).thenReturn(calendar.timeInMillis)
        val viewModel = LandingViewModel(mockContext, mMockDataSource, mMockDBDataSource, mockPreferencesUtil) { 0 }
        viewModel.preConditionCheck(isForceFetch = false)
        verify(mMockDataSource, times(1)).getAll()
        verify(mMockDBDataSource, times(0)).getAll()
    }

    @Test
    fun verifyOutput_shouldNotTriggerDataSourceGetAllWhenTimeNotExpiredAndNotForceRefresh() {
        val mockContext = mock(Context::class.java)
        val mockPreferencesUtil = mock(PreferencesUtil::class.java)
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR, 1)
        `when`(mockPreferencesUtil.getResponseTimeout()).thenReturn(calendar.timeInMillis)
        val viewModel = LandingViewModel(mockContext, mMockDataSource, mMockDBDataSource, mockPreferencesUtil) { 0 }
        viewModel.preConditionCheck(isForceFetch = false)
        verify(mMockDataSource, times(0)).getAll()
    }

    @Test
    fun verifyOutput_shouldTriggerDBSourceGetAllWhenTimeNotExpired() {
        val mockContext = mock(Context::class.java)
        val mockPreferencesUtil = mock(PreferencesUtil::class.java)
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR, 1)
        `when`(mockPreferencesUtil.getResponseTimeout()).thenReturn(calendar.timeInMillis)
        val viewModel = LandingViewModel(mockContext, mMockDataSource, mMockDBDataSource, mockPreferencesUtil) { 0 }
        viewModel.preConditionCheck(isForceFetch = false)
        verify(mMockDataSource, times(0)).getAll()
        verify(mMockDBDataSource, times(1)).getAll()
    }

    @Test
    fun evaluateFlowable_triggerAPIResponseSuccessOnFetchingReposFromServer() {
        val mockContext = mock(Context::class.java)
        val mockPreferencesUtil = mock(PreferencesUtil::class.java)
        `when`(mMockDataSource.getAll()).thenReturn(Flowable.just(listOf(mBaseRepo)))
        val viewModel = LandingViewModel(mockContext, mMockDataSource, mMockDBDataSource, mockPreferencesUtil) { 0 }
        viewModel.preConditionCheck(isForceFetch = false)
            .test()
            .await()
            .assertComplete()
            .assertNoErrors()
            .assertValue {
                it.isNotEmpty() && it[0].name == mBaseRepo.name
            }
    }

    @Test
    fun evaluateFlowable_triggerAPIResponseErrorOnFetchingReposFromServer() {
        val mockContext = mock(Context::class.java)
        val mockPreferencesUtil = mock(PreferencesUtil::class.java)
        `when`(mMockDataSource.getAll()).thenReturn(Flowable.error(Exception("Server Error")))
        val viewModel = LandingViewModel(mockContext, mMockDataSource, mMockDBDataSource, mockPreferencesUtil) { 0 }
        viewModel.preConditionCheck(isForceFetch = false)
            .test()
            .await()
            .assertNotComplete()
            .assertErrorMessage("Server Error")
    }

    @Test
    fun evaluateFlowable_triggerAPIResponseSuccessOnForceRefreshToFetchReposFromServer() {
        val mockContext = mock(Context::class.java)
        val mockPreferencesUtil = mock(PreferencesUtil::class.java)
        `when`(mMockDataSource.getAll()).thenReturn(Flowable.just(listOf(mBaseRepo)))
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR, -1)
        `when`(mockPreferencesUtil.getResponseTimeout()).thenReturn(calendar.timeInMillis)
        val viewModel = LandingViewModel(mockContext, mMockDataSource, mMockDBDataSource, mockPreferencesUtil) { 0 }
        viewModel.preConditionCheck(isForceFetch = true)
            .test()
            .await()
            .assertComplete()
            .assertNoErrors()
            .assertValue {
                it.size == 1 && it[0].name == mBaseRepo.name
            }
    }

    @Test
    fun evaluateFlowable_triggerAPIResponseErrorOnForceRefreshToFetchReposFromServer() {
        val mockContext = mock(Context::class.java)
        val mockPreferencesUtil = mock(PreferencesUtil::class.java)
        `when`(mMockDataSource.getAll()).thenReturn(Flowable.error(Exception("Server Error")))
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR, -1)
        `when`(mockPreferencesUtil.getResponseTimeout()).thenReturn(calendar.timeInMillis)
        val viewModel = LandingViewModel(mockContext, mMockDataSource, mMockDBDataSource, mockPreferencesUtil) { 0 }
        viewModel.preConditionCheck(isForceFetch = true)
            .test()
            .await()
            .assertNotComplete()
            .assertErrorMessage("Server Error")
    }

    @Test
    fun evaluateFlowable_triggerDBResponseSuccessWhenTimeNotExpiredAndForceRefreshFalse() {
        val mockContext = mock(Context::class.java)
        val mockPreferencesUtil = mock(PreferencesUtil::class.java)
        `when`(mMockDBDataSource.getAll()).thenReturn(Flowable.just(listOf(mBaseRepo)))
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR, 1)
        `when`(mockPreferencesUtil.getResponseTimeout()).thenReturn(calendar.timeInMillis)
        val viewModel = LandingViewModel(mockContext, mMockDataSource, mMockDBDataSource, mockPreferencesUtil) { 0 }
        viewModel.preConditionCheck(isForceFetch = false)
            .test()
            .await()
            .assertComplete()
            .assertNoErrors()
            .assertValue {
                it.size == 1 && it[0].name == mBaseRepo.name
            }
    }

    @Test
    fun evaluateFlowable_triggerDBResponseErrorWhenTimeNotExpiredAndForceRefreshFalse() {
        val mockContext = mock(Context::class.java)
        val mockPreferencesUtil = mock(PreferencesUtil::class.java)
        `when`(mMockDBDataSource.getAll()).thenReturn(Flowable.error(Exception("DB Error")))
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR, 1)
        `when`(mockPreferencesUtil.getResponseTimeout()).thenReturn(calendar.timeInMillis)
        val viewModel = LandingViewModel(mockContext, mMockDataSource, mMockDBDataSource, mockPreferencesUtil) { 0 }
        viewModel.preConditionCheck(isForceFetch = false)
            .test()
            .await()
            .await()
            .assertNotComplete()
            .assertErrorMessage("DB Error")
    }

}
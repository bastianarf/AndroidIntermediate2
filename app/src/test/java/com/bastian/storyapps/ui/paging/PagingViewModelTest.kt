package com.bastian.storyapps.ui.paging

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.bastian.storyapps.data.response.StoryItem
import com.bastian.storyapps.paging.StoryRepository
import com.bastian.storyapps.ui.adapter.StoryAdapter
import com.bastian.storyapps.utils.DataDummy
import com.bastian.storyapps.utils.MainDispatcher
import com.bastian.storyapps.utils.TestPagingSource
import com.bastian.storyapps.utils.getOrAwait
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryPagingViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcher()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Test
    fun `Ketika Tidak ada Data Story maka Jumlah Data yang dikembalikan Nol`() = runTest {
        val data: PagingData<StoryItem> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<PagingData<StoryItem>>()
        expectedStory.value = data
        Mockito.`when`(storyRepository.getStory(anyString())).thenReturn(expectedStory)

        val storyViewModel = PagingViewModel(storyRepository)
        val actualStory: PagingData<StoryItem> = storyViewModel.story("token").getOrAwait()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        // Memastikan bahwa data story kosong (null) maka jumlah data yang dikembalikan nol
        assertEquals(0, differ.snapshot().size)
    }

    @Test
    fun `Ketika Berhasil Memuat Data Story maka Data Pasti Tidak Null, Jumlah Data yang dikembalikan Sesuai Yang Diharapkan, dan Data Pertama yang dikembalikan sesuai`() = runTest {
        val storyDummy = DataDummy.dummyStoryResponse()
        val data: PagingData<StoryItem> = TestPagingSource.snapshot(storyDummy)
        val expectedStory = MutableLiveData<PagingData<StoryItem>>()
        expectedStory.value = data
        Mockito.`when`(storyRepository.getStory(anyString())).thenReturn(expectedStory)

        val storyViewModel = PagingViewModel(storyRepository)
        val actualStory: PagingData<StoryItem> = storyViewModel.story("token").getOrAwait()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        // Memastikan bahwa Data Tidak Kosong (Not Null)
        assertNotNull(differ.snapshot())

        // Memastikan bahwa Jumlah Data yang dikembalikan sesuai dengan yang diharapkan
        assertEquals(storyDummy.size, differ.snapshot().size)

        // Memastikan bahwa data pertama yang dikembalikan sesuai
        assertEquals(storyDummy[0], differ.snapshot()[0])
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {
    }
    override fun onRemoved(position: Int, count: Int) {
    }
    override fun onMoved(fromPosition: Int, toPosition: Int) {
    }
    override fun onChanged(position: Int, count: Int, payload: Any?) {
    }
}
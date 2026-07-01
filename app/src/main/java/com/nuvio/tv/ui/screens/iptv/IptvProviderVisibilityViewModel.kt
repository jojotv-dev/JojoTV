package com.nuvio.tv.ui.screens.iptv

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuvio.tv.data.local.IptvSettingsDataStore
import com.streamvault.domain.repository.CategoryRepository
import com.streamvault.domain.repository.ChannelRepository
import com.streamvault.domain.repository.MovieRepository
import com.streamvault.domain.repository.SeriesRepository
import com.streamvault.domain.model.Category
import com.streamvault.domain.model.ContentType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GroupVisibilityItem(
    val id: String,
    val name: String,
    val count: Int,
    val isVisible: Boolean,
)

data class ChannelVisibilityItem(
    val id: Long,
    val name: String,
    val logoUrl: String?,
    val groupTitle: String?,
    val isVisible: Boolean,
)

data class CategoryVisibilityItem(
    val id: Long,
    val name: String,
    val count: Int,
    val isVisible: Boolean,
)

enum class VisibilityTab(val label: String) {
    LIVE("Live TV"),
    MOVIES("Films"),
    SERIES("S\u00e9ries"),
}

data class VisibilityUiState(
    val tab: VisibilityTab = VisibilityTab.LIVE,
    val isLoading: Boolean = true,
    val liveGroups: List<GroupVisibilityItem> = emptyList(),
    val liveChannels: List<ChannelVisibilityItem> = emptyList(),
    val allGroupsVisible: Boolean = true,
    val allChannelsVisible: Boolean = true,
    val movieCategories: List<CategoryVisibilityItem> = emptyList(),
    val allMovieCategoriesVisible: Boolean = true,
    val seriesCategories: List<CategoryVisibilityItem> = emptyList(),
    val allSeriesCategoriesVisible: Boolean = true,
)

private data class CategoryVisibilityData(
    val movieVisibility: Set<String>,
    val seriesVisibility: Set<String>,
    val movieCategories: List<Category>,
    val seriesCategories: List<Category>,
)

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class IptvProviderVisibilityViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dataStore: IptvSettingsDataStore,
    private val channelRepository: ChannelRepository,
    private val movieRepository: MovieRepository,
    private val seriesRepository: SeriesRepository,
) : ViewModel() {

    private val providerId: Long = savedStateHandle.get<String>("providerId")?.toLongOrNull() ?: -1L
    private val _tab = MutableStateFlow(VisibilityTab.LIVE)
    private val _uiState = MutableStateFlow(VisibilityUiState())
    val uiState: StateFlow<VisibilityUiState> = _uiState.asStateFlow()

    init { observeAll() }

    private fun observeAll() {
        viewModelScope.launch {
            // combine max 5 flows -> on imbrique
            val liveData = combine(
                dataStore.visibilitySettings(providerId, ContentType.LIVE),
                channelRepository.getCategories(providerId),
                channelRepository.getChannels(providerId),
            ) { vis, liveCategories, channels ->
                Triple(vis, liveCategories, channels)
            }

            val catData = combine(
                dataStore.visibilitySettings(providerId, ContentType.MOVIE),
                dataStore.visibilitySettings(providerId, ContentType.SERIES),
                movieRepository.getCategories(providerId),
                seriesRepository.getCategories(providerId),
            ) { movieVis, seriesVis, movieCats, seriesCats ->
                CategoryVisibilityData(
                    movieVisibility = movieVis.hiddenGroupIds,
                    seriesVisibility = seriesVis.hiddenGroupIds,
                    movieCategories = movieCats,
                    seriesCategories = seriesCats,
                )
            }

            combine(
                _tab,
                liveData,
                catData,
            ) { tab, (vis, liveCategories, channels), categoryData ->

                val hiddenGroups: Set<String> = vis.hiddenGroupIds
                val hiddenChannelIds: Set<Long> = vis.hiddenChannelIds
                    .mapNotNull { it.toLongOrNull() }.toSet()

                val liveGroups = liveCategories.map { cat ->
                    GroupVisibilityItem(
                        id = cat.id.toString(),
                        name = cat.name,
                        count = cat.count,
                        isVisible = cat.id.toString() !in hiddenGroups,
                    )
                }

                val liveChannels = channels.map { ch ->
                    ChannelVisibilityItem(
                        id = ch.id,
                        name = ch.name,
                        logoUrl = ch.logoUrl,
                        groupTitle = ch.categoryName,
                        isVisible = ch.id !in hiddenChannelIds,
                    )
                }

                val movieCategories = categoryData.movieCategories.map { cat ->
                    CategoryVisibilityItem(
                        id = cat.id,
                        name = cat.name,
                        count = cat.count,
                        isVisible = cat.id.toString() !in categoryData.movieVisibility,
                    )
                }

                val seriesCategories = categoryData.seriesCategories.map { cat ->
                    CategoryVisibilityItem(
                        id = cat.id,
                        name = cat.name,
                        count = cat.count,
                        isVisible = cat.id.toString() !in categoryData.seriesVisibility,
                    )
                }

                VisibilityUiState(
                    tab = tab,
                    isLoading = false,
                    liveGroups = liveGroups,
                    liveChannels = liveChannels,
                    allGroupsVisible = liveGroups.all { it.isVisible },
                    allChannelsVisible = liveChannels.all { it.isVisible },
                    movieCategories = movieCategories,
                    allMovieCategoriesVisible = movieCategories.all { it.isVisible },
                    seriesCategories = seriesCategories,
                    allSeriesCategoriesVisible = seriesCategories.all { it.isVisible },
                )
            }.collect { _uiState.value = it }
        }
    }

    fun selectTab(tab: VisibilityTab) { _tab.value = tab }

    fun toggleGroup(groupId: String, visible: Boolean) {
        viewModelScope.launch { dataStore.setGroupVisible(providerId, ContentType.LIVE, groupId, visible) }
    }
    fun toggleAllGroups(visible: Boolean) {
        viewModelScope.launch {
            _uiState.value.liveGroups.forEach { dataStore.setGroupVisible(providerId, ContentType.LIVE, it.id, visible) }
        }
    }
    fun toggleChannel(channelId: Long, visible: Boolean) {
        viewModelScope.launch { dataStore.setChannelVisible(providerId, channelId, visible) }
    }
    fun toggleAllChannels(visible: Boolean) {
        viewModelScope.launch {
            _uiState.value.liveChannels.forEach { dataStore.setChannelVisible(providerId, it.id, visible) }
        }
    }
    fun toggleMovieCategory(categoryId: Long, visible: Boolean) {
        viewModelScope.launch { dataStore.setGroupVisible(providerId, ContentType.MOVIE, categoryId.toString(), visible) }
    }
    fun toggleAllMovieCategories(visible: Boolean) {
        viewModelScope.launch {
            _uiState.value.movieCategories.forEach { dataStore.setGroupVisible(providerId, ContentType.MOVIE, it.id.toString(), visible) }
        }
    }
    fun toggleSeriesCategory(categoryId: Long, visible: Boolean) {
        viewModelScope.launch { dataStore.setGroupVisible(providerId, ContentType.SERIES, categoryId.toString(), visible) }
    }
    fun toggleAllSeriesCategories(visible: Boolean) {
        viewModelScope.launch {
            _uiState.value.seriesCategories.forEach { dataStore.setGroupVisible(providerId, ContentType.SERIES, it.id.toString(), visible) }
        }
    }
}

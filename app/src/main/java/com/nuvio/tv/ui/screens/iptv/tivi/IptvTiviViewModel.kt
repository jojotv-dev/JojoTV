package com.nuvio.tv.ui.screens.iptv.tivi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.streamvault.domain.model.Category
import com.streamvault.domain.model.Channel
import com.streamvault.domain.model.ContentType
import com.streamvault.domain.model.Movie
import com.streamvault.domain.model.Program
import com.streamvault.domain.model.Provider
import com.streamvault.domain.model.Series
import com.streamvault.domain.repository.ChannelRepository
import com.streamvault.domain.repository.EpgRepository
import com.streamvault.domain.repository.MovieRepository
import com.streamvault.domain.repository.ProviderRepository
import com.streamvault.domain.repository.SeriesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── Onglets sidebar interne ──────────────────────────────────────────────────

enum class TiviTab(val label: String, val contentType: ContentType) {
    LIVE("Live TV", ContentType.LIVE),
    MOVIES("Films", ContentType.MOVIE),
    SERIES("Séries", ContentType.SERIES),
}

// ── Data classes UI ──────────────────────────────────────────────────────────

data class TiviGroup(val id: Long, val name: String)

data class TiviProviderNode(
    val provider: Provider,
    val groups: List<TiviGroup> = emptyList(),
    val isExpanded: Boolean = false,
)

data class TiviEpgRow(val channel: Channel, val programs: List<Program>)

sealed class TiviContent {
    data class LiveContent(
        val channels: List<Channel> = emptyList(),
        val focusedChannel: Channel? = null,
        val currentProgram: Program? = null,
        val nextProgram: Program? = null,
        val epgRows: List<TiviEpgRow> = emptyList(),
        val isLoading: Boolean = false,
    ) : TiviContent()
    data class MoviesContent(
        val movies: List<Movie> = emptyList(),
        val isLoading: Boolean = false,
    ) : TiviContent()
    data class SeriesContent(
        val series: List<Series> = emptyList(),
        val isLoading: Boolean = false,
    ) : TiviContent()
    object Empty : TiviContent()
}

data class TiviUiState(
    val selectedTab: TiviTab = TiviTab.LIVE,
    val providerNodes: List<TiviProviderNode> = emptyList(),
    val selectedGroupId: Long? = null,
    val selectedProviderId: Long? = null,
    val isLoadingProviders: Boolean = true,
    val content: TiviContent = TiviContent.Empty,
)

// ── ViewModel ────────────────────────────────────────────────────────────────

@HiltViewModel
class IptvTiviViewModel @Inject constructor(
    private val providerRepository: ProviderRepository,
    private val channelRepository: ChannelRepository,
    private val epgRepository: EpgRepository,
    private val movieRepository: MovieRepository,
    private val seriesRepository: SeriesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TiviUiState())
    val uiState: StateFlow<TiviUiState> = _uiState.asStateFlow()

    init { loadProviders() }

    private fun loadProviders() {
        viewModelScope.launch {
            providerRepository.getProviders()
                .catch { }
                .collect { providers ->
                    _uiState.update {
                        it.copy(
                            providerNodes = providers.map { p -> TiviProviderNode(provider = p) },
                            isLoadingProviders = false,
                        )
                    }
                }
        }
    }

    fun selectTab(tab: TiviTab) {
        if (_uiState.value.selectedTab == tab) return
        _uiState.update {
            it.copy(
                selectedTab = tab,
                providerNodes = it.providerNodes.map { n -> n.copy(isExpanded = false, groups = emptyList()) },
                selectedGroupId = null,
                selectedProviderId = null,
                content = TiviContent.Empty,
            )
        }
    }

    fun toggleProvider(providerId: Long) {
        val node = _uiState.value.providerNodes.firstOrNull { it.provider.id == providerId } ?: return
        if (!node.isExpanded && node.groups.isEmpty()) {
            viewModelScope.launch {
                val tab = _uiState.value.selectedTab
                val categoriesFlow: Flow<List<Category>> = when (tab) {
                    TiviTab.LIVE    -> channelRepository.getCategories(providerId)
                    TiviTab.MOVIES  -> movieRepository.getCategories(providerId)
                    TiviTab.SERIES  -> seriesRepository.getCategories(providerId)
                }
                categoriesFlow.catch { }.first().let { cats ->
                    val groups = cats
                        .filter { c -> when (tab) {
                            TiviTab.LIVE   -> c.type == ContentType.LIVE
                            TiviTab.MOVIES -> c.type == ContentType.MOVIE
                            TiviTab.SERIES -> c.type == ContentType.SERIES
                        }}
                        .map { TiviGroup(it.id, it.name) }
                    _uiState.update { state ->
                        state.copy(providerNodes = state.providerNodes.map { n ->
                            if (n.provider.id == providerId) n.copy(groups = groups, isExpanded = true) else n
                        })
                    }
                }
            }
        } else {
            _uiState.update { state ->
                state.copy(providerNodes = state.providerNodes.map { n ->
                    if (n.provider.id == providerId) n.copy(isExpanded = !n.isExpanded) else n
                })
            }
        }
    }

    fun selectGroup(providerId: Long, groupId: Long) {
        _uiState.update {
            it.copy(
                selectedGroupId = groupId,
                selectedProviderId = providerId,
                content = when (it.selectedTab) {
                    TiviTab.LIVE   -> TiviContent.LiveContent(isLoading = true)
                    TiviTab.MOVIES -> TiviContent.MoviesContent(isLoading = true)
                    TiviTab.SERIES -> TiviContent.SeriesContent(isLoading = true)
                }
            )
        }
        when (_uiState.value.selectedTab) {
            TiviTab.LIVE   -> loadChannels(providerId, groupId)
            TiviTab.MOVIES -> loadMovies(providerId, groupId)
            TiviTab.SERIES -> loadSeries(providerId, groupId)
        }
    }

    // ── Live ────────────────────────────────────────────────────────────────

    private fun loadChannels(providerId: Long, groupId: Long) {
        viewModelScope.launch {
            channelRepository.getChannelsByCategory(providerId, groupId)
                .catch { }
                .collect { channels ->
                    _uiState.update { state ->
                        val current = state.content as? TiviContent.LiveContent ?: TiviContent.LiveContent()
                        state.copy(content = current.copy(channels = channels, isLoading = false))
                    }
                    if (channels.isNotEmpty()) {
                        val ch = channels.first()
                        focusChannel(providerId, ch)
                        loadEpgForGroup(providerId, channels)
                    }
                }
        }
    }

    fun focusChannel(providerId: Long, channel: Channel) {
        _uiState.update { state ->
            val current = state.content as? TiviContent.LiveContent ?: return@update state
            state.copy(content = current.copy(focusedChannel = channel))
        }
        viewModelScope.launch {
            val epgId = channel.epgChannelId ?: channel.id.toString()
            epgRepository.getNowAndNext(providerId, epgId).catch { }.collect { (now, next) ->
                _uiState.update { state ->
                    val current = state.content as? TiviContent.LiveContent ?: return@update state
                    state.copy(content = current.copy(currentProgram = now, nextProgram = next))
                }
            }
        }
    }

    private fun loadEpgForGroup(providerId: Long, channels: List<Channel>) {
        if (channels.isEmpty()) return
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val end = now + 4 * 60 * 60 * 1000L
            val epgIds = channels.map { it.epgChannelId ?: it.id.toString() }
            epgRepository.getProgramsForChannels(providerId, epgIds, now, end).catch { }.collect { map ->
                val rows = channels.map { ch ->
                    TiviEpgRow(ch, map[ch.epgChannelId ?: ch.id.toString()] ?: emptyList())
                }
                _uiState.update { state ->
                    val current = state.content as? TiviContent.LiveContent ?: return@update state
                    state.copy(content = current.copy(epgRows = rows))
                }
            }
        }
    }

    // ── Movies ───────────────────────────────────────────────────────────────

    private fun loadMovies(providerId: Long, groupId: Long) {
        viewModelScope.launch {
            movieRepository.getMoviesByCategory(providerId, groupId).catch { }.collect { movies ->
                _uiState.update { state ->
                    state.copy(content = TiviContent.MoviesContent(movies = movies, isLoading = false))
                }
            }
        }
    }

    // ── Series ───────────────────────────────────────────────────────────────

    private fun loadSeries(providerId: Long, groupId: Long) {
        viewModelScope.launch {
            seriesRepository.getSeriesByCategory(providerId, groupId).catch { }.collect { series ->
                _uiState.update { state ->
                    state.copy(content = TiviContent.SeriesContent(series = series, isLoading = false))
                }
            }
        }
    }
}

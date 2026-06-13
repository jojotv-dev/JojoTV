package com.nuvio.tv.ui.screens.iptv

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.streamvault.domain.model.Category
import com.streamvault.domain.model.Channel
import com.streamvault.domain.model.ContentType
import com.streamvault.domain.model.Provider
import com.streamvault.domain.repository.CategoryRepository
import com.streamvault.domain.repository.ChannelRepository
import com.streamvault.domain.repository.ProviderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PickerStep {
    object ProviderList : PickerStep
    data class CategoryList(val provider: Provider) : PickerStep
    data class ChannelList(val provider: Provider, val category: Category) : PickerStep
    data class SearchResults(val provider: Provider) : PickerStep
}

@HiltViewModel
class IptvChannelPickerViewModel @Inject constructor(
    private val providerRepository: ProviderRepository,
    private val categoryRepository: CategoryRepository,
    private val channelRepository: ChannelRepository
) : ViewModel() {

    private val _step = MutableStateFlow<PickerStep>(PickerStep.ProviderList)
    val step: StateFlow<PickerStep> = _step.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val providers: StateFlow<List<Provider>> = providerRepository.getProviders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels: StateFlow<List<Channel>> = _channels.asStateFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val searchResults: StateFlow<List<Channel>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            val s = _step.value
            val provider = when (s) {
                is PickerStep.SearchResults -> s.provider
                is PickerStep.CategoryList  -> s.provider
                is PickerStep.ChannelList   -> s.provider
                else -> return@flatMapLatest flowOf(emptyList())
            }
            if (query.isBlank()) flowOf(emptyList())
            else channelRepository.searchChannels(provider.id, query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun selectProvider(provider: Provider) {
        _step.value = PickerStep.CategoryList(provider)
        viewModelScope.launch {
            categoryRepository.getCategories(provider.id)
                .collect { cats ->
                    _categories.value = cats.filter { it.type == ContentType.LIVE && !it.isVirtual }
                }
        }
    }

    fun selectCategory(provider: Provider, category: Category) {
        _step.value = PickerStep.ChannelList(provider, category)
        viewModelScope.launch {
            channelRepository.getChannelsByCategory(provider.id, category.id)
                .collect { _channels.value = it }
        }
    }

    fun openSearch(provider: Provider) {
        _step.value = PickerStep.SearchResults(provider)
    }

    fun updateQuery(q: String) { _searchQuery.value = q }

    fun goBack() {
        _step.value = when (val s = _step.value) {
            is PickerStep.CategoryList  -> PickerStep.ProviderList
            is PickerStep.ChannelList   -> PickerStep.CategoryList(s.provider)
            is PickerStep.SearchResults -> PickerStep.CategoryList(s.provider)
            else -> PickerStep.ProviderList
        }
    }

    fun reset() {
        _step.value = PickerStep.ProviderList
        _searchQuery.value = ""
        _categories.value = emptyList()
        _channels.value = emptyList()
    }
}
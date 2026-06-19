package com.nuvio.tv.ui.screens.iptv.tivi

import android.content.Context
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.streamvault.domain.model.Channel
import com.streamvault.domain.model.Result
import com.streamvault.domain.repository.ChannelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MiniPlayerState(
    val channel: Channel? = null,
    val streamUrl: String? = null,
    val headers: Map<String, String> = emptyMap(),
    val isReady: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class TiviMiniPlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val channelRepository: ChannelRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(MiniPlayerState())
    val state: StateFlow<MiniPlayerState> = _state.asStateFlow()

    @OptIn(UnstableApi::class)
    val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build().also {
        it.playWhenReady = true
        it.volume = 0f  // mute dans le mini lecteur
        it.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    _state.value = _state.value.copy(isReady = true, isLoading = false, errorMessage = null)
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                _state.value = _state.value.copy(
                    isReady = false,
                    isLoading = false,
                    errorMessage = error.localizedMessage ?: "Impossible de lire cette chaîne",
                )
            }
        })
    }

    /** Résout l'URL et les en-têtes provider avant de lancer le mini lecteur. */
    @OptIn(UnstableApi::class)
    fun loadChannel(channel: Channel) {
        _state.value = MiniPlayerState(channel = channel, isLoading = true)
        viewModelScope.launch {
            when (val result = channelRepository.getStreamInfo(channel)) {
                is Result.Success -> {
                    val streamInfo = result.data
                    val httpFactory = DefaultHttpDataSource.Factory()
                        .setDefaultRequestProperties(streamInfo.headers)
                    streamInfo.userAgent?.takeIf { it.isNotBlank() }?.let(httpFactory::setUserAgent)
                    val mediaSource = DefaultMediaSourceFactory(
                        DefaultDataSource.Factory(context, httpFactory)
                    ).createMediaSource(MediaItem.fromUri(streamInfo.url))

                    _state.value = MiniPlayerState(
                        channel = channel,
                        streamUrl = streamInfo.url,
                        headers = streamInfo.headers,
                        isLoading = true,
                    )
                    exoPlayer.setMediaSource(mediaSource)
                    exoPlayer.prepare()
                    exoPlayer.playWhenReady = true
                }
                is Result.Error -> {
                    _state.value = MiniPlayerState(
                        channel = channel,
                        errorMessage = result.message,
                    )
                }
                Result.Loading -> Unit
            }
        }
    }

    fun stop() {
        exoPlayer.stop()
        _state.value = MiniPlayerState()
    }

    override fun onCleared() {
        exoPlayer.release()
        super.onCleared()
    }
}

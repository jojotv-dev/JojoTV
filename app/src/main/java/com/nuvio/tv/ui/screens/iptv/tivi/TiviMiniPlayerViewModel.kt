package com.nuvio.tv.ui.screens.iptv.tivi

import android.content.Context
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.streamvault.domain.model.Channel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class MiniPlayerState(
    val channel: Channel? = null,
    val streamUrl: String? = null,
    val isReady: Boolean = false,
)

@HiltViewModel
class TiviMiniPlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _state = MutableStateFlow(MiniPlayerState())
    val state: StateFlow<MiniPlayerState> = _state.asStateFlow()

    @OptIn(UnstableApi::class)
    val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build().also {
        it.playWhenReady = true
        it.volume = 0f  // mute dans le mini lecteur
    }

    /** Charge et joue une chaîne dans le mini lecteur. Retourne l'URL chargée. */
    fun loadChannel(channel: Channel, streamUrl: String) {
        _state.value = MiniPlayerState(channel = channel, streamUrl = streamUrl, isReady = true)
        val item = MediaItem.fromUri(streamUrl)
        exoPlayer.setMediaItem(item)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
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
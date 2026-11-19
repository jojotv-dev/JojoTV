package com.nuvio.tv.ui.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nuvio.tv.R
import com.nuvio.tv.data.local.PlayerSettings

@Composable
internal fun PlaybackPhotoSettingsSection(
    settings: PlayerSettings,
    onUpdate: PlaybackSettingsUpdate,
) {
    SettingsToggleRow(
        title = stringResource(R.string.playback_photos_slideshow),
        subtitle = stringResource(R.string.playback_photos_slideshow_desc),
        checked = settings.photoSlideshowEnabled,
        onToggle = { onUpdate { setPhotoSlideshowEnabled(!settings.photoSlideshowEnabled) } },
    )

    SliderSettingsItem(
        title = stringResource(R.string.playback_photos_interval),
        subtitle = stringResource(R.string.playback_photos_interval_desc),
        value = settings.photoSlideshowIntervalSeconds,
        valueText = stringResource(R.string.playback_photos_interval_value, settings.photoSlideshowIntervalSeconds),
        minValue = 1,
        maxValue = 10,
        step = 1,
        onValueChange = { seconds -> onUpdate { setPhotoSlideshowIntervalSeconds(seconds) } },
        enabled = settings.photoSlideshowEnabled,
    )
}

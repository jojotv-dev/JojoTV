package com.nuvio.tv.ui.screens.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import com.nuvio.tv.data.local.ProfileSelectionArtworkSnapshot
import com.nuvio.tv.data.local.ProfileSelectionArtworkStore
import com.nuvio.tv.ui.screens.ambient.AmbientSlide
import com.nuvio.tv.ui.screens.ambient.AmbientSlideLoader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class ProfileSelectionArtworkViewModel @Inject constructor(
    private val ambientSlideLoader: AmbientSlideLoader,
    private val artworkStore: ProfileSelectionArtworkStore,
) : ViewModel() {
    private val _slides = MutableStateFlow<List<AmbientSlide>>(emptyList())
    val slides = _slides.asStateFlow()
    private var loadedProfileId: Int? = null

    suspend fun loadFavoriteSlides(profileId: Int) {
        if (loadedProfileId != profileId) {
            loadedProfileId = profileId
            _slides.value = emptyList()
        }
        try {
            val cachedSlides = try {
                artworkStore.observeFavoriteSlides(profileId).first()
                    .map(ProfileSelectionArtworkSnapshot::toAmbientSlide)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.w("ProfileSelectionArtwork", "Unable to read cached favorite artwork", error)
                emptyList()
            }
            if (cachedSlides.isNotEmpty()) _slides.value = cachedSlides

            val refreshedSlides = ambientSlideLoader.load(
                legacyCatalogKeys = emptyList(),
                profileId = profileId,
                onLibrarySlides = { immediateSlides ->
                    if (cachedSlides.isEmpty() && immediateSlides.isNotEmpty()) {
                        _slides.value = immediateSlides
                        persistFavoriteSlides(
                            profileId,
                            immediateSlides.map(AmbientSlide::toSnapshot),
                        )
                    }
                },
            )
            if (refreshedSlides.isNotEmpty()) {
                _slides.value = refreshedSlides
                persistFavoriteSlides(
                    profileId,
                    refreshedSlides.map(AmbientSlide::toSnapshot),
                )
            }
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            Log.w("ProfileSelectionArtwork", "Unable to load favorite artwork", error)
        }
    }

    private suspend fun persistFavoriteSlides(
        profileId: Int,
        slides: List<ProfileSelectionArtworkSnapshot>,
    ) {
        try {
            artworkStore.saveFavoriteSlides(profileId, slides)
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            Log.w("ProfileSelectionArtwork", "Unable to cache favorite artwork", error)
        }
    }
}

private fun ProfileSelectionArtworkSnapshot.toAmbientSlide() = AmbientSlide(
    id = id,
    title = title,
    backdropUrl = backdropUrl,
    logoUrl = logoUrl,
    genres = genres,
)

private fun AmbientSlide.toSnapshot() = ProfileSelectionArtworkSnapshot(
    id = id,
    title = title,
    backdropUrl = backdropUrl,
    logoUrl = logoUrl,
    genres = genres,
)

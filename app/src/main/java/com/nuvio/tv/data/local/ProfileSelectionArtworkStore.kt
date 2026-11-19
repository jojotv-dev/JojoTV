package com.nuvio.tv.data.local

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nuvio.tv.domain.model.CatalogRow
import com.nuvio.tv.domain.model.MetaPreview
import com.nuvio.tv.domain.model.PosterShape
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

private const val PROFILE_SELECTION_ARTWORK_FEATURE = "profile_selection_artwork"
private const val MAX_PROFILE_SELECTION_ARTWORKS = 96
private const val MAX_PROFILE_SELECTION_SLIDES = 30

data class ProfileSelectionArtworkSnapshot(
    val id: String,
    val title: String,
    val backdropUrl: String,
    val logoUrl: String?,
    val genres: List<String>,
)

@Singleton
class ProfileSelectionArtworkStore @Inject constructor(
    private val factory: ProfileDataStoreFactory
) {
    private val artworkUrlsKey = stringPreferencesKey("home_landscape_artwork_urls")
    private val favoriteSlidesKey = stringPreferencesKey("favorite_ambient_slides")

    fun observeArtworkUrls(profileId: Int): Flow<List<String>> =
        factory.get(profileId, PROFILE_SELECTION_ARTWORK_FEATURE).data.map { preferences ->
            val raw = preferences[artworkUrlsKey].orEmpty()
            if (raw.isBlank()) return@map emptyList()
            runCatching {
                val array = JSONArray(raw)
                List(array.length()) { index -> array.optString(index).trim() }
                    .filter(String::isNotBlank)
            }.getOrDefault(emptyList())
        }

    fun observeFavoriteSlides(profileId: Int): Flow<List<ProfileSelectionArtworkSnapshot>> =
        factory.get(profileId, PROFILE_SELECTION_ARTWORK_FEATURE).data.map { preferences ->
            val raw = preferences[favoriteSlidesKey].orEmpty()
            if (raw.isBlank()) return@map emptyList()
            runCatching {
                val array = JSONArray(raw)
                buildList {
                    for (index in 0 until array.length()) {
                        val item = array.optJSONObject(index) ?: continue
                        val backdropUrl = item.optString("backdropUrl").trim()
                        if (backdropUrl.isBlank()) continue
                        val genresArray = item.optJSONArray("genres")
                        val genres = if (genresArray == null) emptyList() else {
                            List(genresArray.length()) { genreIndex ->
                                genresArray.optString(genreIndex).trim()
                            }.filter(String::isNotBlank)
                        }
                        add(
                            ProfileSelectionArtworkSnapshot(
                                id = item.optString("id").ifBlank { "cached:$profileId:$index" },
                                title = item.optString("title"),
                                backdropUrl = backdropUrl,
                                logoUrl = item.optString("logoUrl")
                                    .takeUnless { it.isBlank() || it == "null" },
                                genres = genres,
                            )
                        )
                    }
                }.take(MAX_PROFILE_SELECTION_SLIDES)
            }.getOrDefault(emptyList())
        }

    suspend fun saveFavoriteSlides(profileId: Int, slides: List<ProfileSelectionArtworkSnapshot>) {
        val store = factory.get(profileId, PROFILE_SELECTION_ARTWORK_FEATURE)
        if (slides.isEmpty()) {
            store.edit { it.remove(favoriteSlidesKey) }
            return
        }

        val encodedSlides = JSONArray().apply {
            slides.asSequence().take(MAX_PROFILE_SELECTION_SLIDES).forEach { slide ->
                put(
                    JSONObject()
                        .put("id", slide.id)
                        .put("title", slide.title)
                        .put("backdropUrl", slide.backdropUrl)
                        .put("logoUrl", slide.logoUrl)
                        .put("genres", JSONArray(slide.genres))
                )
            }
        }.toString()
        store.edit { preferences ->
            if (preferences[favoriteSlidesKey] != encodedSlides) {
                preferences[favoriteSlidesKey] = encodedSlides
            }
        }
    }

    suspend fun saveHomeArtwork(profileId: Int, rows: List<CatalogRow>) {
        val artworkByRow = rows.asSequence()
            .filter { row ->
                row.apiType.equals("movie", ignoreCase = true) ||
                    row.apiType.equals("series", ignoreCase = true) ||
                    row.apiType.equals("tv", ignoreCase = true) ||
                    row.apiType.equals("anime", ignoreCase = true)
            }
            .map { row ->
                row.items.asSequence()
                    .mapNotNull { it.landscapeArtworkUrl() }
                    .distinct()
                    .toList()
            }
            .toList()
        val artworkUrls = buildList {
            var itemIndex = 0
            while (size < MAX_PROFILE_SELECTION_ARTWORKS && artworkByRow.any { itemIndex < it.size }) {
                artworkByRow.forEach { rowArtwork ->
                    rowArtwork.getOrNull(itemIndex)
                        ?.takeIf { it !in this }
                        ?.let(::add)
                }
                itemIndex += 1
            }
        }

        val store = factory.get(profileId, PROFILE_SELECTION_ARTWORK_FEATURE)
        if (artworkUrls.isEmpty()) {
            store.edit { it.remove(artworkUrlsKey) }
            return
        }

        val encodedArtworkUrls = JSONArray(artworkUrls).toString()
        store.edit { preferences ->
            if (preferences[artworkUrlsKey] != encodedArtworkUrls) {
                preferences[artworkUrlsKey] = encodedArtworkUrls
            }
        }
    }
}

private fun MetaPreview.landscapeArtworkUrl(): String? =
    landscapePoster?.takeIf(String::isNotBlank)
        ?: background?.takeIf(String::isNotBlank)
        ?: poster?.takeIf { posterShape == PosterShape.LANDSCAPE && it.isNotBlank() }

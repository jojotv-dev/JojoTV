package com.streamvault.data.di

import android.content.Context
import androidx.room.Room
import com.streamvault.data.local.StreamVaultDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StreamVaultDataModule {

    @Provides
    @Singleton
    fun provideStreamVaultDatabase(@ApplicationContext context: Context): StreamVaultDatabase =
        Room.databaseBuilder(context, StreamVaultDatabase::class.java, "streamvault.db")
            .addMigrations(*StreamVaultDatabase.ALL_MIGRATIONS)
            .build()

    @Provides @Singleton fun provideProviderDao(db: StreamVaultDatabase) = db.providerDao()
    @Provides @Singleton fun provideChannelDao(db: StreamVaultDatabase) = db.channelDao()
    @Provides @Singleton fun provideMovieDao(db: StreamVaultDatabase) = db.movieDao()
    @Provides @Singleton fun provideSeriesDao(db: StreamVaultDatabase) = db.seriesDao()
    @Provides @Singleton fun provideCategoryDao(db: StreamVaultDatabase) = db.categoryDao()
    @Provides @Singleton fun provideCatalogSyncDao(db: StreamVaultDatabase) = db.catalogSyncDao()
    @Provides @Singleton fun provideProgramDao(db: StreamVaultDatabase) = db.programDao()
    @Provides @Singleton fun provideFavoriteDao(db: StreamVaultDatabase) = db.favoriteDao()
    @Provides @Singleton fun provideVirtualGroupDao(db: StreamVaultDatabase) = db.virtualGroupDao()
    @Provides @Singleton fun providePlaybackHistoryDao(db: StreamVaultDatabase) = db.playbackHistoryDao()
    @Provides @Singleton fun provideTmdbIdentityDao(db: StreamVaultDatabase) = db.tmdbIdentityDao()
    @Provides @Singleton fun provideSearchHistoryDao(db: StreamVaultDatabase) = db.searchHistoryDao()
    @Provides @Singleton fun provideSearchDao(db: StreamVaultDatabase) = db.searchDao()
    @Provides @Singleton fun provideSyncMetadataDao(db: StreamVaultDatabase) = db.syncMetadataDao()
    @Provides @Singleton fun provideMovieCategoryHydrationDao(db: StreamVaultDatabase) = db.movieCategoryHydrationDao()
    @Provides @Singleton fun provideSeriesCategoryHydrationDao(db: StreamVaultDatabase) = db.seriesCategoryHydrationDao()
    @Provides @Singleton fun provideEpgSourceDao(db: StreamVaultDatabase) = db.epgSourceDao()
    @Provides @Singleton fun provideProviderEpgSourceDao(db: StreamVaultDatabase) = db.providerEpgSourceDao()
    @Provides @Singleton fun provideEpgChannelDao(db: StreamVaultDatabase) = db.epgChannelDao()
    @Provides @Singleton fun provideEpgProgrammeDao(db: StreamVaultDatabase) = db.epgProgrammeDao()
    @Provides @Singleton fun provideChannelEpgMappingDao(db: StreamVaultDatabase) = db.channelEpgMappingDao()
    @Provides @Singleton fun provideRecordingScheduleDao(db: StreamVaultDatabase) = db.recordingScheduleDao()
    @Provides @Singleton fun provideRecordingRunDao(db: StreamVaultDatabase) = db.recordingRunDao()
    @Provides @Singleton fun provideProgramReminderDao(db: StreamVaultDatabase) = db.programReminderDao()
    @Provides @Singleton fun provideRecordingStorageDao(db: StreamVaultDatabase) = db.recordingStorageDao()
    @Provides @Singleton fun providePlaybackCompatibilityDao(db: StreamVaultDatabase) = db.playbackCompatibilityDao()
    @Provides @Singleton fun provideXtreamContentIndexDao(db: StreamVaultDatabase) = db.xtreamContentIndexDao()
    @Provides @Singleton fun provideXtreamIndexJobDao(db: StreamVaultDatabase) = db.xtreamIndexJobDao()
    @Provides @Singleton fun provideXtreamLiveOnboardingDao(db: StreamVaultDatabase) = db.xtreamLiveOnboardingDao()
    @Provides @Singleton fun provideChannelPreferenceDao(db: StreamVaultDatabase) = db.channelPreferenceDao()
    @Provides @Singleton fun provideEpisodeDao(db: StreamVaultDatabase) = db.episodeDao()
    @Provides @Singleton fun provideCombinedM3uProfileDao(db: StreamVaultDatabase) = db.combinedM3uProfileDao()
    @Provides @Singleton fun provideCombinedM3uProfileMemberDao(db: StreamVaultDatabase) = db.combinedM3uProfileMemberDao()
    @Provides @Singleton fun providePlaybackCompatDao(db: StreamVaultDatabase) = db.playbackCompatibilityDao()
}

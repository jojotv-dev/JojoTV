package com.nuvio.tv.core.di

import com.nuvio.tv.core.profile.ProfileScopedCredentialStore
import com.nuvio.tv.data.local.ProfileDataStore
import com.nuvio.tv.data.mediaserver.AndroidServerPersistence
import com.nuvio.tv.data.mediaserver.ServerRepository
import com.nuvio.tv.data.mediaserver.smb.SmbProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Module
@InstallIn(SingletonComponent::class)
object MediaServerModule {
    @Provides
    @Singleton
    fun repository(
        persistence: AndroidServerPersistence,
        profileDataStore: ProfileDataStore
    ): ServerRepository {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        val repository = ServerRepository(
            persistence = persistence,
            providers = listOf(SmbProvider()),
            scope = scope
        )
        scope.launch { profileDataStore.activeProfileId.collect(repository::selectProfile) }
        return repository
    }

    @Provides
    @IntoSet
    fun credentialStore(repository: ServerRepository): ProfileScopedCredentialStore = repository
}

package com.streamvault.data.manager

import android.content.Context
import com.streamvault.domain.manager.DriveAccount
import com.streamvault.domain.manager.DriveAuthState
import com.streamvault.domain.manager.DriveBackupArtifact
import com.streamvault.domain.manager.DriveBackupSyncManager
import com.streamvault.domain.manager.DriveSyncStatus
import com.streamvault.domain.manager.DriveSignInRequest
import com.streamvault.domain.manager.ProviderCredentials
import com.streamvault.domain.model.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleDriveBackupSyncManager @Inject constructor(
    @ApplicationContext private val context: Context
) : DriveBackupSyncManager {
    override val authState: Flow<DriveAuthState> = flowOf(DriveAuthState.SignedOut)
    override val syncStatus: Flow<DriveSyncStatus> = flowOf(DriveSyncStatus())
    override suspend fun beginSignIn(): Result<DriveSignInRequest> = Result.error("Google Drive not available in JojoTV")
    override suspend fun completeSignIn(signInData: Any?): Result<DriveAccount> = Result.error("Google Drive not available in JojoTV")
    override suspend fun signOut(): Result<Unit> = Result.success(Unit)
    override suspend fun pushBackup(): Result<DriveSyncStatus> = Result.error("Google Drive not available in JojoTV")
    override suspend fun pullBackup(): Result<DriveBackupArtifact> = Result.error("Google Drive not available in JojoTV")
    override suspend fun pushCredentials(credentials: List<ProviderCredentials>): Result<Unit> = Result.error("Google Drive not available in JojoTV")
    override suspend fun pullCredentials(): Result<List<ProviderCredentials>> = Result.success(emptyList())
}
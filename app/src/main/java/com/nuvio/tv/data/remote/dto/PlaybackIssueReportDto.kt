package com.nuvio.tv.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlaybackIssueReportRequestDto(
    @param:Json(name = "schemaVersion") val schemaVersion: Int,
    @param:Json(name = "createdAtMs") val createdAtMs: Long,
    @param:Json(name = "app") val app: PlaybackIssueAppDto,
    @param:Json(name = "device") val device: PlaybackIssueDeviceDto,
    @param:Json(name = "content") val content: PlaybackIssueContentDto,
    @param:Json(name = "stream") val stream: PlaybackIssueStreamDto,
    @param:Json(name = "player") val player: PlaybackIssuePlayerDto,
    @param:Json(name = "loading") val loading: PlaybackIssueLoadingDto,
    @param:Json(name = "error") val error: PlaybackIssueErrorDto,
    @param:Json(name = "diagnostics") val diagnostics: PlaybackIssueDiagnosticsDto,
    @param:Json(name = "playbackSettings") val playbackSettings: PlaybackIssuePlaybackSettingsDto? = null,
    @param:Json(name = "playbackAnalytics") val playbackAnalytics: PlaybackIssuePlaybackAnalyticsDto? = null
)

@JsonClass(generateAdapter = true)
data class PlaybackIssueAppDto(
    @param:Json(name = "applicationId") val applicationId: String,
    @param:Json(name = "versionName") val versionName: String,
    @param:Json(name = "versionCode") val versionCode: Long,
    @param:Json(name = "debugBuild") val debugBuild: Boolean
)

@JsonClass(generateAdapter = true)
data class PlaybackIssueDeviceDto(
    @param:Json(name = "manufacturer") val manufacturer: String,
    @param:Json(name = "brand") val brand: String,
    @param:Json(name = "model") val model: String,
    @param:Json(name = "product") val product: String,
    @param:Json(name = "androidRelease") val androidRelease: String,
    @param:Json(name = "sdkInt") val sdkInt: Int,
    @param:Json(name = "supportedAbis") val supportedAbis: List<String>
)

@JsonClass(generateAdapter = true)
data class PlaybackIssueContentDto(
    @param:Json(name = "title") val title: String?,
    @param:Json(name = "contentName") val contentName: String?,
    @param:Json(name = "contentId") val contentId: String?,
    @param:Json(name = "contentType") val contentType: String?,
    @param:Json(name = "videoId") val videoId: String?,
    @param:Json(name = "season") val season: Int?,
    @param:Json(name = "episode") val episode: Int?,
    @param:Json(name = "episodeTitle") val episodeTitle: String?,
    @param:Json(name = "releaseYear") val releaseYear: String?
)

@JsonClass(generateAdapter = true)
data class PlaybackIssueStreamDto(
    @param:Json(name = "host") val host: String?,
    @param:Json(name = "scheme") val scheme: String?,
    @param:Json(name = "port") val port: Int?,
    @param:Json(name = "urlHash") val urlHash: String?,
    @param:Json(name = "urlWithoutQueryHash") val urlWithoutQueryHash: String?,
    @param:Json(name = "fileExtension") val fileExtension: String?,
    @param:Json(name = "mimeType") val mimeType: String?,
    @param:Json(name = "streamName") val streamName: String?,
    @param:Json(name = "addonName") val addonName: String?,
    @param:Json(name = "videoHash") val videoHash: String?,
    @param:Json(name = "videoSize") val videoSize: Long?,
    @param:Json(name = "requestHeaderNames") val requestHeaderNames: List<String>,
    @param:Json(name = "responseHeaderNames") val responseHeaderNames: List<String>
)

@JsonClass(generateAdapter = true)
data class PlaybackIssuePlayerDto(
    @param:Json(name = "engine") val engine: String,
    @param:Json(name = "positionMs") val positionMs: Long?,
    @param:Json(name = "durationMs") val durationMs: Long?,
    @param:Json(name = "bufferedPositionMs") val bufferedPositionMs: Long?,
    @param:Json(name = "audioTrack") val audioTrack: String?,
    @param:Json(name = "subtitleTrack") val subtitleTrack: String?
)

@JsonClass(generateAdapter = true)
data class PlaybackIssueErrorDto(
    @param:Json(name = "displayMessage") val displayMessage: String?,
    @param:Json(name = "errorCode") val errorCode: Int?,
    @param:Json(name = "errorCodeName") val errorCodeName: String?,
    @param:Json(name = "exceptionClass") val exceptionClass: String?,
    @param:Json(name = "causeClass") val causeClass: String?,
    @param:Json(name = "causeMessage") val causeMessage: String?,
    @param:Json(name = "httpStatus") val httpStatus: Int?
)

@JsonClass(generateAdapter = true)
data class PlaybackIssueLoadingDto(
    @param:Json(name = "phase") val phase: String,
    @param:Json(name = "message") val message: String?,
    @param:Json(name = "progress") val progress: Float?,
    @param:Json(name = "elapsedMs") val elapsedMs: Long,
    @param:Json(name = "phaseElapsedMs") val phaseElapsedMs: Long,
    @param:Json(name = "reportReason") val reportReason: String,
    @param:Json(name = "loadingOverlayVisible") val loadingOverlayVisible: Boolean,
    @param:Json(name = "loadingStatusVisible") val loadingStatusVisible: Boolean,
    @param:Json(name = "hasRenderedFirstFrame") val hasRenderedFirstFrame: Boolean,
    @param:Json(name = "exoPlayerCreated") val exoPlayerCreated: Boolean,
    @param:Json(name = "exoPlaybackState") val exoPlaybackState: Int?,
    @param:Json(name = "exoPlaybackStateName") val exoPlaybackStateName: String?,
    @param:Json(name = "exoIsLoading") val exoIsLoading: Boolean?,
    @param:Json(name = "exoPlayWhenReady") val exoPlayWhenReady: Boolean?,
    @param:Json(name = "mpvAttached") val mpvAttached: Boolean,
    @param:Json(name = "startupRetryCount") val startupRetryCount: Int,
    @param:Json(name = "errorRetryCount") val errorRetryCount: Int,
    @param:Json(name = "timeoutRecoveryAttempts") val timeoutRecoveryAttempts: Int,
    @param:Json(name = "isLoadingAddonSubtitles") val isLoadingAddonSubtitles: Boolean,
    @param:Json(name = "addonSubtitlesCount") val addonSubtitlesCount: Int,
    @param:Json(name = "isLoadingSourceStreams") val isLoadingSourceStreams: Boolean,
    @param:Json(name = "isLoadingEpisodeStreams") val isLoadingEpisodeStreams: Boolean,
    @param:Json(name = "rawEventLines") val rawEventLines: List<String>,
    @param:Json(name = "events") val events: List<PlaybackIssueLoadingEventDto>
)

@JsonClass(generateAdapter = true)
data class PlaybackIssueLoadingEventDto(
    @param:Json(name = "timeMs") val timeMs: Long,
    @param:Json(name = "elapsedMs") val elapsedMs: Long,
    @param:Json(name = "phase") val phase: String,
    @param:Json(name = "message") val message: String?,
    @param:Json(name = "progress") val progress: Float?,
    @param:Json(name = "detail") val detail: String?
)

@JsonClass(generateAdapter = true)
data class PlaybackIssueDiagnosticsDto(
    @param:Json(name = "timestampMs") val timestampMs: Long,
    @param:Json(name = "host") val host: String,
    @param:Json(name = "hdrCapsKnown") val hdrCapsKnown: Boolean,
    @param:Json(name = "displayDv") val displayDv: Boolean,
    @param:Json(name = "displayHdr10") val displayHdr10: Boolean,
    @param:Json(name = "displayHdr10Plus") val displayHdr10Plus: Boolean,
    @param:Json(name = "codecDv7Supported") val codecDv7Supported: Boolean,
    @param:Json(name = "dv81DecoderName") val dv81DecoderName: String?,
    @param:Json(name = "bridgeReady") val bridgeReady: Boolean,
    @param:Json(name = "bridgeVersion") val bridgeVersion: String?,
    @param:Json(name = "bridgeReason") val bridgeReason: String?,
    @param:Json(name = "dv7ModeRequested") val dv7ModeRequested: String,
    @param:Json(name = "dv7ModeEffective") val dv7ModeEffective: String,
    @param:Json(name = "dv7AutoDecision") val dv7AutoDecision: String?,
    @param:Json(name = "bufferEngineEnabled") val bufferEngineEnabled: Boolean,
    @param:Json(name = "parallelNetworkEnabled") val parallelNetworkEnabled: Boolean,
    @param:Json(name = "firstFrameMs") val firstFrameMs: Long,
    @param:Json(name = "dv7DoviCalls") val dv7DoviCalls: Int,
    @param:Json(name = "dv7DoviSuccess") val dv7DoviSuccess: Int,
    @param:Json(name = "dv7DoviSignalRewrites") val dv7DoviSignalRewrites: Int,
    @param:Json(name = "dvSourceProfile") val dvSourceProfile: String?,
    @param:Json(name = "videoResolution") val videoResolution: String?,
    @param:Json(name = "videoCodec") val videoCodec: String?,
    @param:Json(name = "videoHdrType") val videoHdrType: String?,
    @param:Json(name = "rebufferCount") val rebufferCount: Int,
    @param:Json(name = "rebufferTotalMs") val rebufferTotalMs: Long,
    @param:Json(name = "result") val result: String
)

@JsonClass(generateAdapter = true)
data class PlaybackIssuePlaybackSettingsDto(
    @param:Json(name = "playerPreference") val playerPreference: String,
    @param:Json(name = "internalPlayerEngine") val internalPlayerEngine: String,
    @param:Json(name = "resolvedInternalPlayerEngine") val resolvedInternalPlayerEngine: String,
    @param:Json(name = "autoSwitchInternalPlayerOnError") val autoSwitchInternalPlayerOnError: Boolean,
    @param:Json(name = "decoderPriority") val decoderPriority: Int,
    @param:Json(name = "decoderPriorityName") val decoderPriorityName: String,
    @param:Json(name = "effectiveDecoderPriority") val effectiveDecoderPriority: Int,
    @param:Json(name = "effectiveDecoderPriorityName") val effectiveDecoderPriorityName: String,
    @param:Json(name = "downmixEnabled") val downmixEnabled: Boolean,
    @param:Json(name = "audioOutputChannels") val audioOutputChannels: String,
    @param:Json(name = "maintainOriginalAudioOnDownmix") val maintainOriginalAudioOnDownmix: Boolean,
    @param:Json(name = "tunnelingEnabled") val tunnelingEnabled: Boolean,
    @param:Json(name = "tunnelingEffective") val tunnelingEffective: Boolean,
    @param:Json(name = "forceOpticalPassthrough") val forceOpticalPassthrough: Boolean,
    @param:Json(name = "skipSilence") val skipSilence: Boolean,
    @param:Json(name = "audioAmplificationDb") val audioAmplificationDb: Int,
    @param:Json(name = "centerMixLevelDb") val centerMixLevelDb: Int,
    @param:Json(name = "persistAudioAmplification") val persistAudioAmplification: Boolean,
    @param:Json(name = "rememberAudioDelayPerDevice") val rememberAudioDelayPerDevice: Boolean,
    @param:Json(name = "preferredAudioLanguage") val preferredAudioLanguage: String,
    @param:Json(name = "secondaryPreferredAudioLanguage") val secondaryPreferredAudioLanguage: String?,
    @param:Json(name = "preferredSubtitleLanguage") val preferredSubtitleLanguage: String,
    @param:Json(name = "secondaryPreferredSubtitleLanguage") val secondaryPreferredSubtitleLanguage: String?,
    @param:Json(name = "useForcedSubtitles") val useForcedSubtitles: Boolean,
    @param:Json(name = "showOnlyPreferredSubtitleLanguages") val showOnlyPreferredSubtitleLanguages: Boolean,
    @param:Json(name = "useLibass") val useLibass: Boolean,
    @param:Json(name = "activePlayerUsesLibass") val activePlayerUsesLibass: Boolean,
    @param:Json(name = "libassRenderType") val libassRenderType: String,
    @param:Json(name = "addonSubtitleStartupMode") val addonSubtitleStartupMode: String = "SIDECAR",
    @param:Json(name = "externalPlayerForwardSubtitles") val externalPlayerForwardSubtitles: Boolean,
    @param:Json(name = "subtitleOrganizationMode") val subtitleOrganizationMode: String,
    @param:Json(name = "loadingOverlayEnabled") val loadingOverlayEnabled: Boolean,
    @param:Json(name = "showPlayerLoadingStatus") val showPlayerLoadingStatus: Boolean,
    @param:Json(name = "playbackIssueReportsEnabled") val playbackIssueReportsEnabled: Boolean,
    @param:Json(name = "dv5ToDv81Enabled") val dv5ToDv81Enabled: Boolean,
    @param:Json(name = "dv7ToDv81PreserveMappingEnabled") val dv7ToDv81PreserveMappingEnabled: Boolean,
    @param:Json(name = "dv7HandlingMode") val dv7HandlingMode: String,
    @param:Json(name = "dv7LibdoviModeOverride") val dv7LibdoviModeOverride: Int,
    @param:Json(name = "stripHdr10PlusSei") val stripHdr10PlusSei: Boolean,
    @param:Json(name = "mpvHardwareDecodeMode") val mpvHardwareDecodeMode: String,
    @param:Json(name = "frameRateMatchingMode") val frameRateMatchingMode: String,
    @param:Json(name = "resolutionMatchingEnabled") val resolutionMatchingEnabled: Boolean,
    @param:Json(name = "resizeMode") val resizeMode: Int,
    @param:Json(name = "aspectMode") val aspectMode: String,
    @param:Json(name = "bufferEngineEnabled") val bufferEngineEnabled: Boolean,
    @param:Json(name = "minBufferMs") val minBufferMs: Int,
    @param:Json(name = "maxBufferMs") val maxBufferMs: Int,
    @param:Json(name = "bufferForPlaybackMs") val bufferForPlaybackMs: Int,
    @param:Json(name = "bufferForPlaybackAfterRebufferMs") val bufferForPlaybackAfterRebufferMs: Int,
    @param:Json(name = "targetBufferSizeMb") val targetBufferSizeMb: Int,
    @param:Json(name = "backBufferDurationMs") val backBufferDurationMs: Int,
    @param:Json(name = "effectiveBackBufferDurationMs") val effectiveBackBufferDurationMs: Int,
    @param:Json(name = "retainBackBufferFromKeyframe") val retainBackBufferFromKeyframe: Boolean,
    @param:Json(name = "parallelNetworkEnabled") val parallelNetworkEnabled: Boolean,
    @param:Json(name = "bufferBudgetManaged") val bufferBudgetManaged: Boolean,
    @param:Json(name = "allowLargeTargetBuffer") val allowLargeTargetBuffer: Boolean,
    @param:Json(name = "vodCacheEnabled") val vodCacheEnabled: Boolean,
    @param:Json(name = "vodCacheSizeMode") val vodCacheSizeMode: String,
    @param:Json(name = "vodCacheSizeMb") val vodCacheSizeMb: Int,
    @param:Json(name = "useParallelConnections") val useParallelConnections: Boolean,
    @param:Json(name = "parallelConnectionCount") val parallelConnectionCount: Int,
    @param:Json(name = "parallelChunkSizeKb") val parallelChunkSizeKb: Int,
    @param:Json(name = "enableHttp2") val enableHttp2: Boolean,
    @param:Json(name = "nuvioPerformanceModeEnabled") val nuvioPerformanceModeEnabled: Boolean,
    @param:Json(name = "streamAutoPlayMode") val streamAutoPlayMode: String,
    @param:Json(name = "streamAutoPlaySource") val streamAutoPlaySource: String,
    @param:Json(name = "streamAutoPlayNextEpisodeEnabled") val streamAutoPlayNextEpisodeEnabled: Boolean,
    @param:Json(name = "streamAutoPlayPreferBingeGroupForNextEpisode") val streamAutoPlayPreferBingeGroupForNextEpisode: Boolean,
    @param:Json(name = "streamAutoPlayReuseBingeGroup") val streamAutoPlayReuseBingeGroup: Boolean,
    @param:Json(name = "streamAutoPlayTimeoutSeconds") val streamAutoPlayTimeoutSeconds: Int,
    @param:Json(name = "stillWatchingEnabled") val stillWatchingEnabled: Boolean,
    @param:Json(name = "stillWatchingEpisodeThreshold") val stillWatchingEpisodeThreshold: Int,
    @param:Json(name = "nextEpisodeThresholdMode") val nextEpisodeThresholdMode: String,
    @param:Json(name = "nextEpisodeThresholdPercent") val nextEpisodeThresholdPercent: Float,
    @param:Json(name = "nextEpisodeThresholdMinutesBeforeEnd") val nextEpisodeThresholdMinutesBeforeEnd: Float,
    @param:Json(name = "streamReuseLastLinkEnabled") val streamReuseLastLinkEnabled: Boolean,
    @param:Json(name = "streamReuseLastLinkCacheHours") val streamReuseLastLinkCacheHours: Int
)

@JsonClass(generateAdapter = true)
data class PlaybackIssuePlaybackAnalyticsDto(
    @param:Json(name = "schemaVersion") val schemaVersion: Int,
    @param:Json(name = "sessionStartedAtMs") val sessionStartedAtMs: Long,
    @param:Json(name = "capturedAtMs") val capturedAtMs: Long,
    @param:Json(name = "elapsedMs") val elapsedMs: Long,
    @param:Json(name = "clickToFirstFrameMs") val clickToFirstFrameMs: Long?,
    @param:Json(name = "initToFirstFrameMs") val initToFirstFrameMs: Long?,
    @param:Json(name = "startPositionMs") val startPositionMs: Long?,
    @param:Json(name = "eventCount") val eventCount: Int,
    @param:Json(name = "lastEventElapsedMs") val lastEventElapsedMs: Long?,
    @param:Json(name = "playbackState") val playbackState: Int?,
    @param:Json(name = "playbackStateName") val playbackStateName: String?,
    @param:Json(name = "playWhenReady") val playWhenReady: Boolean?,
    @param:Json(name = "isPlaying") val isPlaying: Boolean?,
    @param:Json(name = "isLoading") val isLoading: Boolean?,
    @param:Json(name = "playbackSpeed") val playbackSpeed: Float?,
    @param:Json(name = "playbackPitch") val playbackPitch: Float?,
    @param:Json(name = "positionMs") val positionMs: Long?,
    @param:Json(name = "bufferedPositionMs") val bufferedPositionMs: Long?,
    @param:Json(name = "durationMs") val durationMs: Long?,
    @param:Json(name = "bufferedPercentage") val bufferedPercentage: Int?,
    @param:Json(name = "firstFrameElapsedMs") val firstFrameElapsedMs: Long?,
    @param:Json(name = "renderedFirstFrameCount") val renderedFirstFrameCount: Int,
    @param:Json(name = "rebufferCount") val rebufferCount: Int,
    @param:Json(name = "rebufferTotalMs") val rebufferTotalMs: Long,
    @param:Json(name = "currentRebufferMs") val currentRebufferMs: Long,
    @param:Json(name = "positionStallCount") val positionStallCount: Int,
    @param:Json(name = "longestPositionStallMs") val longestPositionStallMs: Long,
    @param:Json(name = "droppedFrames") val droppedFrames: Int,
    @param:Json(name = "maxDroppedFramesInEvent") val maxDroppedFramesInEvent: Int,
    @param:Json(name = "videoDecoderName") val videoDecoderName: String?,
    @param:Json(name = "videoDecoderInitMs") val videoDecoderInitMs: Long?,
    @param:Json(name = "videoDecoderReleaseCount") val videoDecoderReleaseCount: Int,
    @param:Json(name = "videoRenderedOutputBuffers") val videoRenderedOutputBuffers: Int?,
    @param:Json(name = "videoDroppedBuffers") val videoDroppedBuffers: Int?,
    @param:Json(name = "videoMaxConsecutiveDroppedBuffers") val videoMaxConsecutiveDroppedBuffers: Int?,
    @param:Json(name = "videoFrameProcessingOffsetAverageUs") val videoFrameProcessingOffsetAverageUs: Long?,
    @param:Json(name = "videoFormat") val videoFormat: PlaybackIssuePlaybackFormatDto?,
    @param:Json(name = "audioDecoderName") val audioDecoderName: String?,
    @param:Json(name = "audioDecoderInitMs") val audioDecoderInitMs: Long?,
    @param:Json(name = "audioDecoderReleaseCount") val audioDecoderReleaseCount: Int,
    @param:Json(name = "audioUnderrunCount") val audioUnderrunCount: Int,
    @param:Json(name = "audioUnderrunBufferSize") val audioUnderrunBufferSize: Int?,
    @param:Json(name = "audioUnderrunBufferSizeMs") val audioUnderrunBufferSizeMs: Long?,
    @param:Json(name = "audioUnderrunElapsedSinceLastFeedMs") val audioUnderrunElapsedSinceLastFeedMs: Long?,
    @param:Json(name = "audioFormat") val audioFormat: PlaybackIssuePlaybackFormatDto?,
    @param:Json(name = "bandwidthEstimateBps") val bandwidthEstimateBps: Long?,
    @param:Json(name = "bandwidthTransferDurationMs") val bandwidthTransferDurationMs: Int?,
    @param:Json(name = "bandwidthBytesTransferred") val bandwidthBytesTransferred: Long?,
    @param:Json(name = "loadStartedCount") val loadStartedCount: Int,
    @param:Json(name = "loadCompletedCount") val loadCompletedCount: Int,
    @param:Json(name = "loadCanceledCount") val loadCanceledCount: Int,
    @param:Json(name = "loadErrorCount") val loadErrorCount: Int,
    @param:Json(name = "totalBytesLoaded") val totalBytesLoaded: Long,
    @param:Json(name = "lastLoad") val lastLoad: PlaybackIssuePlaybackLoadDto?,
    @param:Json(name = "lastLoadError") val lastLoadError: PlaybackIssuePlaybackLoadErrorDto?,
    @param:Json(name = "rawEventLines") val rawEventLines: List<String>,
    @param:Json(name = "events") val events: List<PlaybackIssuePlaybackEventDto>,
    @param:Json(name = "rawEvents") val rawEvents: List<String>,
    @param:Json(name = "deepExoEvents") val deepExoEvents: List<PlaybackIssuePlaybackEventDto>,
    @param:Json(name = "exoEvents") val exoEvents: List<PlaybackIssuePlaybackEventDto>,
    @param:Json(name = "stutterSignals") val stutterSignals: List<PlaybackIssuePlaybackEventDto>,
    @param:Json(name = "healthSnapshots") val healthSnapshots: List<PlaybackIssuePlaybackHealthSnapshotDto>,
    @param:Json(name = "startupStages") val startupStages: List<PlaybackIssueLoadingEventDto>
)

@JsonClass(generateAdapter = true)
data class PlaybackIssuePlaybackHealthSnapshotDto(
    @param:Json(name = "timeMs") val timeMs: Long,
    @param:Json(name = "elapsedMs") val elapsedMs: Long,
    @param:Json(name = "playbackState") val playbackState: String?,
    @param:Json(name = "playWhenReady") val playWhenReady: Boolean?,
    @param:Json(name = "isPlaying") val isPlaying: Boolean?,
    @param:Json(name = "isLoading") val isLoading: Boolean?,
    @param:Json(name = "playbackSpeed") val playbackSpeed: Float?,
    @param:Json(name = "playbackPitch") val playbackPitch: Float?,
    @param:Json(name = "positionMs") val positionMs: Long?,
    @param:Json(name = "bufferedPositionMs") val bufferedPositionMs: Long?,
    @param:Json(name = "durationMs") val durationMs: Long?,
    @param:Json(name = "bufferedPercentage") val bufferedPercentage: Int?,
    @param:Json(name = "droppedFrames") val droppedFrames: Int,
    @param:Json(name = "audioUnderrunCount") val audioUnderrunCount: Int,
    @param:Json(name = "rebufferCount") val rebufferCount: Int,
    @param:Json(name = "rebufferTotalMs") val rebufferTotalMs: Long,
    @param:Json(name = "bandwidthEstimateBps") val bandwidthEstimateBps: Long?,
    @param:Json(name = "totalBytesLoaded") val totalBytesLoaded: Long,
    @param:Json(name = "loadStartedCount") val loadStartedCount: Int,
    @param:Json(name = "loadCompletedCount") val loadCompletedCount: Int,
    @param:Json(name = "loadCanceledCount") val loadCanceledCount: Int,
    @param:Json(name = "loadErrorCount") val loadErrorCount: Int
)

@JsonClass(generateAdapter = true)
data class PlaybackIssuePlaybackFormatDto(
    @param:Json(name = "trackType") val trackType: String?,
    @param:Json(name = "sampleMimeType") val sampleMimeType: String?,
    @param:Json(name = "containerMimeType") val containerMimeType: String?,
    @param:Json(name = "codecs") val codecs: String?,
    @param:Json(name = "id") val id: String?,
    @param:Json(name = "label") val label: String?,
    @param:Json(name = "language") val language: String?,
    @param:Json(name = "width") val width: Int?,
    @param:Json(name = "height") val height: Int?,
    @param:Json(name = "frameRate") val frameRate: Float?,
    @param:Json(name = "bitrate") val bitrate: Int?,
    @param:Json(name = "channelCount") val channelCount: Int?,
    @param:Json(name = "sampleRate") val sampleRate: Int?,
    @param:Json(name = "colorTransfer") val colorTransfer: Int?,
    @param:Json(name = "selectionFlags") val selectionFlags: Int?,
    @param:Json(name = "roleFlags") val roleFlags: Int?,
    @param:Json(name = "support") val support: String?,
    @param:Json(name = "decoderReuseResult") val decoderReuseResult: String?,
    @param:Json(name = "decoderDiscardReasons") val decoderDiscardReasons: Int?
)

@JsonClass(generateAdapter = true)
data class PlaybackIssuePlaybackLoadDto(
    @param:Json(name = "host") val host: String?,
    @param:Json(name = "scheme") val scheme: String?,
    @param:Json(name = "dataType") val dataType: String?,
    @param:Json(name = "trackType") val trackType: String?,
    @param:Json(name = "httpMethod") val httpMethod: String?,
    @param:Json(name = "position") val position: Long?,
    @param:Json(name = "length") val length: Long?,
    @param:Json(name = "durationMs") val durationMs: Long?,
    @param:Json(name = "bytesLoaded") val bytesLoaded: Long?,
    @param:Json(name = "responseHeaderNames") val responseHeaderNames: List<String>
)

@JsonClass(generateAdapter = true)
data class PlaybackIssuePlaybackLoadErrorDto(
    @param:Json(name = "host") val host: String?,
    @param:Json(name = "dataType") val dataType: String?,
    @param:Json(name = "trackType") val trackType: String?,
    @param:Json(name = "exceptionClass") val exceptionClass: String?,
    @param:Json(name = "message") val message: String?,
    @param:Json(name = "httpStatus") val httpStatus: Int?,
    @param:Json(name = "wasCanceled") val wasCanceled: Boolean,
    @param:Json(name = "bytesLoaded") val bytesLoaded: Long?,
    @param:Json(name = "durationMs") val durationMs: Long?
)

@JsonClass(generateAdapter = true)
data class PlaybackIssuePlaybackEventDto(
    @param:Json(name = "timeMs") val timeMs: Long,
    @param:Json(name = "elapsedMs") val elapsedMs: Long,
    @param:Json(name = "name") val name: String,
    @param:Json(name = "playbackState") val playbackState: String?,
    @param:Json(name = "positionMs") val positionMs: Long?,
    @param:Json(name = "bufferedPositionMs") val bufferedPositionMs: Long?,
    @param:Json(name = "details") val details: Map<String, String>
)

@JsonClass(generateAdapter = true)
data class PlaybackIssueReportResponseDto(
    @param:Json(name = "reportId") val reportId: String? = null,
    @param:Json(name = "id") val id: String? = null
)

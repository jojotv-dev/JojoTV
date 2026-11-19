package io.ktor.client.plugins;

import androidx.media3.container.NalUnitUtil;
import kotlin.Metadata;

/* JADX INFO: loaded from: classes5.dex */
@Metadata(d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\t\bÂ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0014\u0010\u0005\u001a\u00020\u00048\u0002X\u0082T¢\u0006\u0006\n\u0004\b\u0005\u0010\u0006R\u0014\u0010\u0007\u001a\u00020\u00048\u0002X\u0082T¢\u0006\u0006\n\u0004\b\u0007\u0010\u0006R\u0014\u0010\b\u001a\u00020\u00048\u0002X\u0082T¢\u0006\u0006\n\u0004\b\b\u0010\u0006R\u0014\u0010\t\u001a\u00020\u00048\u0006X\u0086T¢\u0006\u0006\n\u0004\b\t\u0010\u0006R\u0014\u0010\n\u001a\u00020\u00048\u0006X\u0086T¢\u0006\u0006\n\u0004\b\n\u0010\u0006R\u0014\u0010\u000b\u001a\u00020\u00048\u0006X\u0086T¢\u0006\u0006\n\u0004\b\u000b\u0010\u0006R\u0014\u0010\f\u001a\u00020\u00048\u0006X\u0086T¢\u0006\u0006\n\u0004\b\f\u0010\u0006¨\u0006\r"}, d2 = {"Lio/ktor/client/plugins/Messages;", "", "<init>", "()V", "", "USE_STREAMING_SYNTAX", "Ljava/lang/String;", "API_WILL_BE_REMOVED", "SHARE_USE_CASE", "SAVE_BODY_ENABLED_MESSAGE", "SAVE_BODY_DISABLED_MESSAGE", "PLUGIN_DEPRECATED_MESSAGE", "SKIP_SAVING_BODY_MESSAGE", "ktor-client-core"}, k = 1, mv = {2, 1, 0}, xi = NalUnitUtil.H265_NAL_UNIT_TYPE_UNSPECIFIED)
final class Messages {
    private static final String API_WILL_BE_REMOVED = "This API is deprecated and will be removed in Ktor 4.0.0";
    public static final Messages INSTANCE = new Messages();
    public static final String PLUGIN_DEPRECATED_MESSAGE = "This plugin is no longer needed.\nThis API is deprecated and will be removed in Ktor 4.0.0";
    public static final String SAVE_BODY_DISABLED_MESSAGE = "It is no longer possible to disable body saving for all requests. Use client.prepareRequest(...).execute { ... } syntax to prevent saving the body in memory.\n\nThis API is deprecated and will be removed in Ktor 4.0.0\nIf you were relying on this functionality, share your use case by commenting on this issue: https://youtrack.jetbrains.com/issue/KTOR-8367/";
    public static final String SAVE_BODY_ENABLED_MESSAGE = "The SaveBodyPlugin plugin is deprecated and can be safely removed. Request bodies are now saved in memory by default for all non-streaming responses.";
    private static final String SHARE_USE_CASE = "If you were relying on this functionality, share your use case by commenting on this issue: https://youtrack.jetbrains.com/issue/KTOR-8367/";
    public static final String SKIP_SAVING_BODY_MESSAGE = "Skipping of body saving for a specific request is no longer allowed.\nUse client.prepareRequest(...).execute { ... } syntax to prevent saving the body in memory.\n\nThis API is deprecated and will be removed in Ktor 4.0.0\nIf you were relying on this functionality, share your use case by commenting on this issue: https://youtrack.jetbrains.com/issue/KTOR-8367/";
    private static final String USE_STREAMING_SYNTAX = "Use client.prepareRequest(...).execute { ... } syntax to prevent saving the body in memory.";

    private Messages() {
    }
}

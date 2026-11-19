package com.nuvio.tv.core.plugin;

import androidx.media3.container.NalUnitUtil;
import kotlin.Metadata;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000\u0016\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0003X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u0007"}, d2 = {"TAG", "", "PLUGIN_TIMEOUT_MS", "", "MAX_FETCH_RESPONSE_BYTES", "", "MAX_FETCH_BODY_CHARS", "app_fullRelease"}, k = 2, mv = {2, 3, 0}, xi = NalUnitUtil.H265_NAL_UNIT_TYPE_UNSPECIFIED)
public final class PluginRuntimeKt {
    private static final int MAX_FETCH_BODY_CHARS = 1048576;
    private static final int MAX_FETCH_RESPONSE_BYTES = 1048576;
    private static final long PLUGIN_TIMEOUT_MS = 60000;
    private static final String TAG = "PluginRuntime";
}

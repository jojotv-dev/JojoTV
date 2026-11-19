package com.nuvio.tv.core.plugin.cloudstream;

import androidx.media3.container.NalUnitUtil;
import kotlin.Metadata;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000\u001c\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\b\n\u0000\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0003X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0004\u001a\u00020\u0003X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0007\u001a\u00020\bX\u0082T¢\u0006\u0002\n\u0000¨\u0006\t"}, d2 = {"TAG", "", "EXECUTION_TIMEOUT_MS", "", "LOADLINKS_TIMEOUT_MS", "MIN_TITLE_SIMILARITY", "", "MAX_ALT_TITLES", "", "app_fullRelease"}, k = 2, mv = {2, 3, 0}, xi = NalUnitUtil.H265_NAL_UNIT_TYPE_UNSPECIFIED)
public final class ExternalExtensionRunnerKt {
    private static final long EXECUTION_TIMEOUT_MS = 120000;
    private static final long LOADLINKS_TIMEOUT_MS = 60000;
    private static final int MAX_ALT_TITLES = 8;
    private static final double MIN_TITLE_SIMILARITY = 0.5d;
    private static final String TAG = "ExtExtensionRunner";
}

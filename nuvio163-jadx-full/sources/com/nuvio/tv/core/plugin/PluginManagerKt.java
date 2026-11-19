package com.nuvio.tv.core.plugin;

import androidx.media3.container.NalUnitUtil;
import kotlin.Metadata;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000\u0018\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0003\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0003X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0004\u001a\u00020\u0003X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0007\u001a\u00020\u0006X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\b\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000¨\u0006\t"}, d2 = {"TAG", "", "MAX_CONCURRENT_SCRAPERS", "", "MAX_RESULT_ITEMS", "MAX_RESPONSE_SIZE", "", "SCRAPER_TIMEOUT_MS", "MANIFEST_SUFFIX", "app_fullRelease"}, k = 2, mv = {2, 3, 0}, xi = NalUnitUtil.H265_NAL_UNIT_TYPE_UNSPECIFIED)
public final class PluginManagerKt {
    private static final String MANIFEST_SUFFIX = "/manifest.json";
    private static final int MAX_CONCURRENT_SCRAPERS = 10;
    private static final long MAX_RESPONSE_SIZE = 5242880;
    private static final int MAX_RESULT_ITEMS = 150;
    private static final long SCRAPER_TIMEOUT_MS = 120000;
    private static final String TAG = "PluginManager";
}

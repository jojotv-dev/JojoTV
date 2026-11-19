package com.lagradost.api;

import androidx.media3.container.NalUnitUtil;
import kotlin.Metadata;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0014\u0010\u0004\u001a\u00020\u0005X\u0086D¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\b"}, d2 = {"Lcom/lagradost/api/BuildConfig;", "", "<init>", "()V", "MDL_API_KEY", "", "getMDL_API_KEY", "()Ljava/lang/String;", "library"}, k = 1, mv = {2, 3, 0}, xi = NalUnitUtil.H265_NAL_UNIT_TYPE_UNSPECIFIED)
public final class BuildConfig {
    public static final BuildConfig INSTANCE = new BuildConfig();
    private static final String MDL_API_KEY = "null";

    private BuildConfig() {
    }

    public final String getMDL_API_KEY() {
        return MDL_API_KEY;
    }
}

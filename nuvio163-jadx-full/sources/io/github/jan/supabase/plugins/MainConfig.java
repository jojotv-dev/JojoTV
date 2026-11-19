package io.github.jan.supabase.plugins;

import androidx.media3.container.NalUnitUtil;
import kotlin.Metadata;

/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\b\b\u0016\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003R\u001c\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\tR\u001c\u0010\n\u001a\u0004\u0018\u00010\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\u0007\"\u0004\b\f\u0010\t¨\u0006\r"}, d2 = {"Lio/github/jan/supabase/plugins/MainConfig;", "", "<init>", "()V", "customUrl", "", "getCustomUrl", "()Ljava/lang/String;", "setCustomUrl", "(Ljava/lang/String;)V", "jwtToken", "getJwtToken", "setJwtToken", "supabase-kt_release"}, k = 1, mv = {2, 2, 0}, xi = NalUnitUtil.H265_NAL_UNIT_TYPE_UNSPECIFIED)
public class MainConfig {
    private String customUrl;
    private String jwtToken;

    public final String getCustomUrl() {
        return this.customUrl;
    }

    public final String getJwtToken() {
        return this.jwtToken;
    }

    public final void setCustomUrl(String str) {
        this.customUrl = str;
    }

    public final void setJwtToken(String str) {
        this.jwtToken = str;
    }
}

package io.github.jan.supabase.auth.providers;

import androidx.media3.container.NalUnitUtil;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;

/* JADX INFO: loaded from: classes5.dex */
@Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010!\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010%\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0005\b\u0016\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003R\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u001d\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00060\n¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u001a\u0010\r\u001a\u00020\u000eX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012¨\u0006\u0013"}, d2 = {"Lio/github/jan/supabase/auth/providers/ExternalAuthConfigDefaults;", "", "<init>", "()V", "scopes", "", "", "getScopes", "()Ljava/util/List;", "queryParams", "", "getQueryParams", "()Ljava/util/Map;", "automaticallyOpenUrl", "", "getAutomaticallyOpenUrl", "()Z", "setAutomaticallyOpenUrl", "(Z)V", "auth-kt_release"}, k = 1, mv = {2, 2, 0}, xi = NalUnitUtil.H265_NAL_UNIT_TYPE_UNSPECIFIED)
public class ExternalAuthConfigDefaults {
    private final List<String> scopes = new ArrayList();
    private final Map<String, String> queryParams = new LinkedHashMap();
    private boolean automaticallyOpenUrl = true;

    public final boolean getAutomaticallyOpenUrl() {
        return this.automaticallyOpenUrl;
    }

    public final Map<String, String> getQueryParams() {
        return this.queryParams;
    }

    public final List<String> getScopes() {
        return this.scopes;
    }

    public final void setAutomaticallyOpenUrl(boolean z2) {
        this.automaticallyOpenUrl = z2;
    }
}

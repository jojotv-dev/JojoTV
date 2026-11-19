package com.nuvio.tv.domain.model;

import androidx.media3.container.NalUnitUtil;
import kotlin.Metadata;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\f\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\t\u0010\n\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000b\u001a\u00020\u0003HÆ\u0003J\u001d\u0010\f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0003HÆ\u0001J\u0014\u0010\r\u001a\u00020\u00032\b\u0010\u000e\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u000f\u001a\u00020\u0010HÖ\u0081\u0004J\n\u0010\u0011\u001a\u00020\u0012HÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\b¨\u0006\u0013"}, d2 = {"Lcom/nuvio/tv/domain/model/ServerCapabilities;", "", "emailPasswordAuth", "", "tvLogin", "<init>", "(ZZ)V", "getEmailPasswordAuth", "()Z", "getTvLogin", "component1", "component2", "copy", "equals", "other", "hashCode", "", "toString", "", "app_fullRelease"}, k = 1, mv = {2, 3, 0}, xi = NalUnitUtil.H265_NAL_UNIT_TYPE_UNSPECIFIED)
public final /* data */ class ServerCapabilities {
    public static final int $stable = 0;
    private final boolean emailPasswordAuth;
    private final boolean tvLogin;

    public ServerCapabilities(boolean z2, boolean z10) {
        this.emailPasswordAuth = z2;
        this.tvLogin = z10;
    }

    public static /* synthetic */ ServerCapabilities copy$default(ServerCapabilities serverCapabilities, boolean z2, boolean z10, int i10, Object obj) {
        if ((i10 & 1) != 0) {
            z2 = serverCapabilities.emailPasswordAuth;
        }
        if ((i10 & 2) != 0) {
            z10 = serverCapabilities.tvLogin;
        }
        return serverCapabilities.copy(z2, z10);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final boolean getEmailPasswordAuth() {
        return this.emailPasswordAuth;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final boolean getTvLogin() {
        return this.tvLogin;
    }

    public final ServerCapabilities copy(boolean emailPasswordAuth, boolean tvLogin) {
        return new ServerCapabilities(emailPasswordAuth, tvLogin);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ServerCapabilities)) {
            return false;
        }
        ServerCapabilities serverCapabilities = (ServerCapabilities) other;
        return this.emailPasswordAuth == serverCapabilities.emailPasswordAuth && this.tvLogin == serverCapabilities.tvLogin;
    }

    public final boolean getEmailPasswordAuth() {
        return this.emailPasswordAuth;
    }

    public final boolean getTvLogin() {
        return this.tvLogin;
    }

    public int hashCode() {
        return Boolean.hashCode(this.tvLogin) + (Boolean.hashCode(this.emailPasswordAuth) * 31);
    }

    public String toString() {
        return "ServerCapabilities(emailPasswordAuth=" + this.emailPasswordAuth + ", tvLogin=" + this.tvLogin + ")";
    }
}

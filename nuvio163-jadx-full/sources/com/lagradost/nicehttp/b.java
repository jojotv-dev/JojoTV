package com.lagradost.nicehttp;

import com.liskovsoft.sharedutils.helpers.NetworkHelpers;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class b implements HostnameVerifier {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f7639a;

    public /* synthetic */ b(int i10) {
        this.f7639a = i10;
    }

    @Override // javax.net.ssl.HostnameVerifier
    public final boolean verify(String str, SSLSession sSLSession) {
        switch (this.f7639a) {
            case 0:
                return UtilsKt.ignoreAllSSLErrors$lambda$1(str, sSLSession);
            case 1:
                return NetworkHelpers.getDohURLConnection$lambda$0$0(str, sSLSession);
            default:
                return true;
        }
    }
}

package ul;

import ek.d;
import okhttp3.internal.http2.Http2Connection;

/* JADX INFO: loaded from: classes5.dex */
public final class a implements d {

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final a f59961i = new a("hqc-128", 17669, 2241, 2321);

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final a f59962m = new a("hqc-192", 35851, 4514, 4602);

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public static final a f59963n = new a("hqc-256", 57637, 7237, 7333);

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f59964b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f59965c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final int f59966e;

    public a(String str, int i10, int i11, int i12) {
        this.f59964b = str;
        this.f59965c = i11;
        this.f59966e = i12;
        int i13 = Http2Connection.OKHTTP_CLIENT_WINDOW_SIZE / i10;
    }
}

package mf;

import android.net.Uri;
import android.os.SystemClock;
import androidx.media3.exoplayer.Renderer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class x3 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Uri f38800a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public volatile Map f38801b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final long f38802c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final int f38803d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final int f38804e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public volatile Uri f38805f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public volatile long f38806g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public final ConcurrentHashMap f38807h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final ConcurrentHashMap f38808i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public final AtomicBoolean f38809j;
    public final ConcurrentHashMap.KeySetView k;

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    public final AtomicInteger f38810l;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public volatile long f38811m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public volatile long f38812n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public volatile long f38813o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public volatile long f38814p;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public final AtomicInteger f38815q;

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    public final ConcurrentHashMap.KeySetView f38816r;

    /* JADX INFO: renamed from: s, reason: collision with root package name */
    public final ConcurrentHashMap f38817s;

    /* JADX INFO: renamed from: t, reason: collision with root package name */
    public volatile long f38818t;

    /* JADX INFO: renamed from: u, reason: collision with root package name */
    public volatile long f38819u;

    public x3(Uri requestUri, Map requestHeaders, long j10, int i10, int i11) {
        Intrinsics.checkNotNullParameter(requestUri, "requestUri");
        Intrinsics.checkNotNullParameter(requestHeaders, "requestHeaders");
        this.f38800a = requestUri;
        this.f38801b = requestHeaders;
        this.f38802c = j10;
        this.f38803d = i10;
        this.f38804e = i11;
        this.f38806g = -1L;
        this.f38807h = new ConcurrentHashMap();
        this.f38808i = new ConcurrentHashMap();
        this.f38809j = new AtomicBoolean(false);
        ConcurrentHashMap.KeySetView keySetViewNewKeySet = ConcurrentHashMap.newKeySet();
        Intrinsics.checkNotNullExpressionValue(keySetViewNewKeySet, "newKeySet(...)");
        this.k = keySetViewNewKeySet;
        this.f38810l = new AtomicInteger(Integer.MAX_VALUE);
        this.f38814p = Renderer.DEFAULT_DURATION_TO_PROGRESS_US;
        this.f38815q = new AtomicInteger(0);
        ConcurrentHashMap.KeySetView keySetViewNewKeySet2 = ConcurrentHashMap.newKeySet();
        Intrinsics.checkNotNullExpressionValue(keySetViewNewKeySet2, "newKeySet(...)");
        this.f38816r = keySetViewNewKeySet2;
        this.f38817s = new ConcurrentHashMap();
        this.f38818t = SystemClock.uptimeMillis();
        this.f38819u = -1L;
    }
}

package com.nuvio.vpn.android;

import android.content.Context;
import android.os.SystemClock;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/* JADX INFO: loaded from: classes4.dex */
public final class p0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Context f10955a;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final String f10957c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final int f10958d;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public final boolean f10961g;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public boolean f10964j;
    public boolean k;

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    public boolean f10965l;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f10956b = UUID.randomUUID().toString();

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final long f10959e = SystemClock.elapsedRealtime();

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public final CompletableFuture f10960f = new CompletableFuture();

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public final int f10962h = r0.f10982h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final long f10963i = r0.k.get();

    public p0(Context context, String str, int i10, boolean z2) {
        this.f10955a = context;
        this.f10957c = str;
        this.f10958d = i10;
        this.f10961g = z2;
    }

    public final boolean a() {
        if (this.f10961g) {
            return this.f10962h == r0.f10982h && this.f10963i == r0.k.get();
        }
        return true;
    }
}

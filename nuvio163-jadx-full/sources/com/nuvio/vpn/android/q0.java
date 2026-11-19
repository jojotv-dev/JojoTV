package com.nuvio.vpn.android;

import android.content.Context;
import android.os.SystemClock;
import java.util.concurrent.CompletableFuture;

/* JADX INFO: loaded from: classes4.dex */
public final class q0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Context f10970a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f10971b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f10972c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final CompletableFuture f10973d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final long f10974e = SystemClock.elapsedRealtime();

    public q0(Context context, String str, int i10, CompletableFuture completableFuture) {
        this.f10970a = context;
        this.f10971b = str;
        this.f10972c = i10;
        this.f10973d = completableFuture;
    }
}

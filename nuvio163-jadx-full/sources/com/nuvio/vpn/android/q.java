package com.nuvio.vpn.android;

/* JADX INFO: loaded from: classes4.dex */
public final class q {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final long f10966a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f10967b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final boolean f10968c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final long f10969d;

    public q(long j10, long j11, long j12) {
        this.f10966a = j10;
        this.f10967b = j11;
        this.f10968c = j12 > 0 && System.currentTimeMillis() - j12 <= 180000;
        this.f10969d = 0L;
    }

    public q(long j10, long j11, boolean z2, long j12) {
        this.f10966a = j10;
        this.f10967b = j11;
        this.f10968c = z2;
        this.f10969d = j12;
    }
}

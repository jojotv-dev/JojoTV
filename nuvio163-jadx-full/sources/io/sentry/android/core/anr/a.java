package io.sentry.android.core.anr;

import java.util.Arrays;

/* JADX INFO: loaded from: classes5.dex */
public final class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f24598a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final float f24599b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final StackTraceElement[] f24600c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final int f24601d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final int f24602e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public int f24603f = 1;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public long f24604g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public long f24605h;

    public a(StackTraceElement[] stackTraceElementArr, int i10, int i11, long j10, float f10) {
        this.f24600c = stackTraceElementArr;
        this.f24601d = i10;
        this.f24602e = i11;
        this.f24598a = (i11 - i10) + 1;
        this.f24604g = j10;
        this.f24605h = j10;
        this.f24599b = f10;
    }

    public final StackTraceElement[] a() {
        return (StackTraceElement[]) Arrays.copyOfRange(this.f24600c, this.f24601d, this.f24602e + 1);
    }
}

package qe;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class p9 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final float f49696a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final float f49697b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final String f49698c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final String f49699d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final long f49700e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public final long f49701f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public final long f49702g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public final Float f49703h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final boolean f49704i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public final boolean f49705j;
    public final String k;

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    public final boolean f49706l;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public final boolean f49707m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public final boolean f49708n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public final boolean f49709o;

    public p9(float f10, float f11, String title, String str, long j10, long j11, long j12, Float f12, boolean z2, boolean z10, String str2, boolean z11, boolean z12, boolean z13, int i10) {
        Float f13 = (i10 & 128) != 0 ? null : f12;
        boolean z14 = (i10 & 256) != 0 ? false : z2;
        boolean z15 = (i10 & 512) != 0 ? false : z10;
        String str3 = (i10 & 1024) == 0 ? str2 : null;
        boolean z16 = (i10 & 2048) != 0 ? false : z11;
        boolean z17 = (i10 & 4096) != 0 ? false : z12;
        boolean z18 = (i10 & 8192) != 0 ? false : z13;
        boolean z19 = (i10 & 16384) == 0;
        Intrinsics.checkNotNullParameter(title, "title");
        this.f49696a = f10;
        this.f49697b = f11;
        this.f49698c = title;
        this.f49699d = str;
        this.f49700e = j10;
        this.f49701f = j11;
        this.f49702g = j12;
        this.f49703h = f13;
        this.f49704i = z14;
        this.f49705j = z15;
        this.k = str3;
        this.f49706l = z16;
        this.f49707m = z17;
        this.f49708n = z18;
        this.f49709o = z19;
    }
}

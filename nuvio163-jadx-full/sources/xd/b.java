package xd;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class b {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f63716a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int[] f63717b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int[] f63718c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final int f63719d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final int f63720e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public final int f63721f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public final int f63722g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public final boolean f63723h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final int f63724i;

    public b(int i10, int[] pageHeights, int[] pageQuadCounts, int i11, int i12, int i13, int i14, boolean z2, int i15) {
        Intrinsics.checkNotNullParameter(pageHeights, "pageHeights");
        Intrinsics.checkNotNullParameter(pageQuadCounts, "pageQuadCounts");
        this.f63716a = i10;
        this.f63717b = pageHeights;
        this.f63718c = pageQuadCounts;
        this.f63719d = i11;
        this.f63720e = i12;
        this.f63721f = i13;
        this.f63722g = i14;
        this.f63723h = z2;
        this.f63724i = i15;
    }

    public b() {
        this(0, new int[]{0}, new int[]{0}, 0, 0, 0, 1, false, 0);
    }
}

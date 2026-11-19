package h8;

import androidx.media3.exoplayer.analytics.AnalyticsListener;

/* JADX INFO: loaded from: classes.dex */
public final class a {

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public static final a f20786g;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int[] f20787a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int[] f20788b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final b f20789c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final int f20790d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final int f20791e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public final int f20792f;

    static {
        new a(4201, 4096, 1);
        new a(AnalyticsListener.EVENT_RENDERER_READY_CHANGED, 1024, 1);
        new a(67, 64, 1);
        new a(19, 16, 1);
        f20786g = new a(285, 256, 0);
        new a(301, 256, 1);
    }

    public a(int i10, int i11, int i12) {
        this.f20791e = i10;
        this.f20790d = i11;
        this.f20792f = i12;
        this.f20787a = new int[i11];
        this.f20788b = new int[i11];
        int i13 = 1;
        for (int i14 = 0; i14 < i11; i14++) {
            this.f20787a[i14] = i13;
            i13 *= 2;
            if (i13 >= i11) {
                i13 = (i13 ^ i10) & (i11 - 1);
            }
        }
        for (int i15 = 0; i15 < i11 - 1; i15++) {
            this.f20788b[this.f20787a[i15]] = i15;
        }
        this.f20789c = new b(this, new int[]{0});
    }

    public final int a(int i10, int i11) {
        if (i10 == 0 || i11 == 0) {
            return 0;
        }
        int[] iArr = this.f20788b;
        return this.f20787a[(iArr[i10] + iArr[i11]) % (this.f20790d - 1)];
    }

    public final String toString() {
        StringBuilder sb2 = new StringBuilder("GF(0x");
        sb2.append(Integer.toHexString(this.f20791e));
        sb2.append(',');
        return o2.a.p(sb2, this.f20790d, ')');
    }
}

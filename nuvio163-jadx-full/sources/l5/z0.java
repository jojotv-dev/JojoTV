package l5;

import android.os.Bundle;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public abstract class z0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final t0 f32134b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final s0 f32135c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final t0 f32136d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final s0 f32137e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public static final t0 f32138f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public static final s0 f32139g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public static final t0 f32140h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final s0 f32141i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public static final t0 f32142j;
    public static final s0 k;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final boolean f32143a;

    static {
        int i10 = 2;
        boolean z2 = false;
        f32134b = new t0(z2, i10);
        boolean z10 = true;
        f32135c = new s0(z10, i10);
        int i11 = 3;
        f32136d = new t0(z2, i11);
        f32137e = new s0(z10, i11);
        int i12 = 1;
        f32138f = new t0(z2, i12);
        f32139g = new s0(z10, i12);
        int i13 = 0;
        f32140h = new t0(z2, i13);
        f32141i = new s0(z10, i13);
        int i14 = 4;
        f32142j = new t0(z10, i14);
        k = new s0(z10, i14);
    }

    public z0(boolean z2) {
        this.f32143a = z2;
    }

    public abstract Object a(Bundle bundle, String str);

    public abstract String b();

    public Object c(Object obj, String value) {
        Intrinsics.checkNotNullParameter(value, "value");
        return d(value);
    }

    public abstract Object d(String str);

    public abstract void e(Bundle bundle, String str, Object obj);

    public boolean f(Object obj, Object obj2) {
        return Intrinsics.areEqual(obj, obj2);
    }

    public final String toString() {
        return b();
    }
}

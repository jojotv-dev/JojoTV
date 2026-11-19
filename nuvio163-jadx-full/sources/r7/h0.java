package r7;

import java.util.Comparator;

/* JADX INFO: loaded from: classes.dex */
public final class h0 extends j0 {
    public static j0 g(int i10) {
        if (i10 < 0) {
            return j0.f51284b;
        }
        return i10 > 0 ? j0.f51285c : j0.f51283a;
    }

    @Override // r7.j0
    public final j0 a(int i10, int i11) {
        return g(Integer.compare(i10, i11));
    }

    @Override // r7.j0
    public final j0 b(long j10, long j11) {
        return g(Long.compare(j10, j11));
    }

    @Override // r7.j0
    public final j0 c(Object obj, Object obj2, Comparator comparator) {
        return g(comparator.compare(obj, obj2));
    }

    @Override // r7.j0
    public final j0 d(boolean z2, boolean z10) {
        return g(Boolean.compare(z2, z10));
    }

    @Override // r7.j0
    public final j0 e(boolean z2, boolean z10) {
        return g(Boolean.compare(z10, z2));
    }

    @Override // r7.j0
    public final int f() {
        return 0;
    }
}

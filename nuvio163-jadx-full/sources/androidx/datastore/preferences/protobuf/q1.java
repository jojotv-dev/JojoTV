package androidx.datastore.preferences.protobuf;

import sun.misc.Unsafe;

/* JADX INFO: loaded from: classes3.dex */
public final class q1 extends t1 {
    public q1(Unsafe unsafe) {
        super(unsafe);
    }

    @Override // androidx.datastore.preferences.protobuf.t1
    public final boolean c(long j10, Object obj) {
        if (u1.f2578g) {
            return u1.h(j10, obj) != 0;
        }
        return u1.i(j10, obj) != 0;
    }

    @Override // androidx.datastore.preferences.protobuf.t1
    public final byte d(long j10, Object obj) {
        return u1.f2578g ? u1.h(j10, obj) : u1.i(j10, obj);
    }

    @Override // androidx.datastore.preferences.protobuf.t1
    public final double e(long j10, Object obj) {
        return Double.longBitsToDouble(h(j10, obj));
    }

    @Override // androidx.datastore.preferences.protobuf.t1
    public final float f(long j10, Object obj) {
        return Float.intBitsToFloat(g(j10, obj));
    }

    @Override // androidx.datastore.preferences.protobuf.t1
    public final void k(long j10, Object obj, boolean z2) {
        if (u1.f2578g) {
            u1.l(obj, j10, z2 ? (byte) 1 : (byte) 0);
        } else {
            u1.m(obj, j10, z2 ? (byte) 1 : (byte) 0);
        }
    }

    @Override // androidx.datastore.preferences.protobuf.t1
    public final void l(Object obj, long j10, byte b5) {
        if (u1.f2578g) {
            u1.l(obj, j10, b5);
        } else {
            u1.m(obj, j10, b5);
        }
    }

    @Override // androidx.datastore.preferences.protobuf.t1
    public final void m(Object obj, long j10, double d10) {
        p(j10, obj, Double.doubleToLongBits(d10));
    }

    @Override // androidx.datastore.preferences.protobuf.t1
    public final void n(Object obj, long j10, float f10) {
        o(Float.floatToIntBits(f10), j10, obj);
    }

    @Override // androidx.datastore.preferences.protobuf.t1
    public final boolean s() {
        return false;
    }
}

package u2;

import kotlin.jvm.functions.Function1;
import kotlin.ranges.RangesKt;

/* JADX INFO: loaded from: classes.dex */
public abstract class l1 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public int f58157b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f58158c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public long f58159e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public long f58160i = n1.f58172b;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public long f58161m = 0;

    public l1() {
        long j10 = 0;
        this.f58159e = (j10 & 4294967295L) | (j10 << 32);
    }

    public int a0() {
        return (int) (this.f58159e & 4294967295L);
    }

    public int b0() {
        return (int) (this.f58159e >> 32);
    }

    public final void c0() {
        this.f58157b = RangesKt.coerceIn((int) (this.f58159e >> 32), t3.a.j(this.f58160i), t3.a.h(this.f58160i));
        int iCoerceIn = RangesKt.coerceIn((int) (this.f58159e & 4294967295L), t3.a.i(this.f58160i), t3.a.g(this.f58160i));
        this.f58158c = iCoerceIn;
        int i10 = this.f58157b;
        long j10 = this.f58159e;
        this.f58161m = (((long) ((i10 - ((int) (j10 >> 32))) / 2)) << 32) | (4294967295L & ((long) ((iCoerceIn - ((int) (j10 & 4294967295L))) / 2)));
    }

    public abstract void d0(long j10, float f10, Function1 function1);

    public final void e0(long j10) {
        if (t3.m.a(this.f58159e, j10)) {
            return;
        }
        this.f58159e = j10;
        c0();
    }

    public Object l() {
        return null;
    }

    public final void m0(long j10) {
        if (t3.a.b(this.f58160i, j10)) {
            return;
        }
        this.f58160i = j10;
        c0();
    }
}

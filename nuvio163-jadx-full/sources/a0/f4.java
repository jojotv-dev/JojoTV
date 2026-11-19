package a0;

/* JADX INFO: loaded from: classes.dex */
public final class f4 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f116a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public long f117b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public Object f118c;

    public f4() {
        this.f116a = 1;
        this.f117b = 0L;
    }

    public void a(int i10) {
        if (i10 < 64) {
            this.f117b &= ~(1 << i10);
            return;
        }
        f4 f4Var = (f4) this.f118c;
        if (f4Var != null) {
            f4Var.a(i10 - 64);
        }
    }

    public int b(int i10) {
        f4 f4Var = (f4) this.f118c;
        if (f4Var == null) {
            return i10 >= 64 ? Long.bitCount(this.f117b) : Long.bitCount(this.f117b & ((1 << i10) - 1));
        }
        if (i10 < 64) {
            return Long.bitCount(this.f117b & ((1 << i10) - 1));
        }
        return Long.bitCount(this.f117b) + f4Var.b(i10 - 64);
    }

    public void c() {
        if (((f4) this.f118c) == null) {
            this.f118c = new f4();
        }
    }

    public boolean d(int i10) {
        if (i10 < 64) {
            return (this.f117b & (1 << i10)) != 0;
        }
        c();
        return ((f4) this.f118c).d(i10 - 64);
    }

    public long e(float f10, long j10, boolean z2) {
        long jE;
        if (z2) {
            jE = c2.b.e(this.f117b, j10);
            this.f117b = jE;
        } else {
            jE = c2.b.e(this.f117b, j10);
        }
        if ((((e2) this.f118c) == null ? c2.b.c(jE) : Math.abs(g(jE))) < f10) {
            return 9205357640488583168L;
        }
        if (((e2) this.f118c) == null) {
            long j11 = this.f117b;
            float fC = c2.b.c(j11);
            float fIntBitsToFloat = Float.intBitsToFloat((int) (j11 >> 32)) / fC;
            return c2.b.d(this.f117b, c2.b.f((((long) Float.floatToRawIntBits(Float.intBitsToFloat((int) (j11 & 4294967295L)) / fC)) & 4294967295L) | (((long) Float.floatToRawIntBits(fIntBitsToFloat)) << 32), f10));
        }
        float fG = g(this.f117b) - (Math.signum(g(this.f117b)) * f10);
        long j12 = this.f117b;
        e2 e2Var = (e2) this.f118c;
        e2 e2Var2 = e2.f101c;
        float fIntBitsToFloat2 = Float.intBitsToFloat((int) (e2Var == e2Var2 ? j12 & 4294967295L : j12 >> 32));
        if (((e2) this.f118c) == e2Var2) {
            return (((long) Float.floatToRawIntBits(fIntBitsToFloat2)) & 4294967295L) | (((long) Float.floatToRawIntBits(fG)) << 32);
        }
        return (((long) Float.floatToRawIntBits(fG)) & 4294967295L) | (((long) Float.floatToRawIntBits(fIntBitsToFloat2)) << 32);
    }

    public void f(int i10, boolean z2) {
        if (i10 >= 64) {
            c();
            ((f4) this.f118c).f(i10 - 64, z2);
            return;
        }
        long j10 = this.f117b;
        boolean z10 = (Long.MIN_VALUE & j10) != 0;
        long j11 = (1 << i10) - 1;
        this.f117b = ((j10 & (~j11)) << 1) | (j10 & j11);
        if (z2) {
            j(i10);
        } else {
            a(i10);
        }
        if (z10 || ((f4) this.f118c) != null) {
            c();
            ((f4) this.f118c).f(0, z10);
        }
    }

    public float g(long j10) {
        return Float.intBitsToFloat((int) (((e2) this.f118c) == e2.f101c ? j10 >> 32 : j10 & 4294967295L));
    }

    public boolean h(int i10) {
        if (i10 >= 64) {
            c();
            return ((f4) this.f118c).h(i10 - 64);
        }
        long j10 = 1 << i10;
        long j11 = this.f117b;
        boolean z2 = (j11 & j10) != 0;
        long j12 = j11 & (~j10);
        this.f117b = j12;
        long j13 = j10 - 1;
        this.f117b = (j12 & j13) | Long.rotateRight((~j13) & j12, 1);
        f4 f4Var = (f4) this.f118c;
        if (f4Var != null) {
            if (f4Var.d(0)) {
                j(63);
            }
            ((f4) this.f118c).h(0);
        }
        return z2;
    }

    public void i() {
        this.f117b = 0L;
        f4 f4Var = (f4) this.f118c;
        if (f4Var != null) {
            f4Var.i();
        }
    }

    public void j(int i10) {
        if (i10 < 64) {
            this.f117b |= 1 << i10;
        } else {
            c();
            ((f4) this.f118c).j(i10 - 64);
        }
    }

    public String toString() {
        switch (this.f116a) {
            case 1:
                if (((f4) this.f118c) == null) {
                    return Long.toBinaryString(this.f117b);
                }
                return ((f4) this.f118c).toString() + "xx" + Long.toBinaryString(this.f117b);
            default:
                return super.toString();
        }
    }

    public f4(long j10, e2 e2Var) {
        this.f116a = 0;
        this.f118c = e2Var;
        this.f117b = j10;
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ f4(e2 e2Var, int i10) {
        this(0L, (i10 & 1) != 0 ? null : e2Var);
        this.f116a = 0;
    }
}

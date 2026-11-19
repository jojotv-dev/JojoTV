package t3;

import kf.ge;

/* JADX INFO: loaded from: classes.dex */
public interface c {
    default long F0(long j10) {
        if (j10 == 9205357640488583168L) {
            return 9205357640488583168L;
        }
        float fO0 = o0(i.b(j10));
        return (((long) Float.floatToRawIntBits(o0(i.a(j10)))) & 4294967295L) | (Float.floatToRawIntBits(fO0) << 32);
    }

    default int I(float f10) {
        float fO0 = o0(f10);
        if (Float.isInfinite(fO0)) {
            return Integer.MAX_VALUE;
        }
        return Math.round(fO0);
    }

    default float K(long j10) {
        if (!q.a(p.c(j10), 4294967296L)) {
            j.b("Only Sp can convert to Px");
        }
        return o0(m(j10));
    }

    default float f0(int i10) {
        return i10 / getDensity();
    }

    float getDensity();

    default float h0(float f10) {
        return f10 / getDensity();
    }

    default long i(float f10) {
        float[] fArr = u3.b.f58260a;
        if (l0() < 1.03f) {
            return ge.i0(4294967296L, f10 / l0());
        }
        u3.a aVarA = u3.b.a(l0());
        return ge.i0(4294967296L, aVarA != null ? aVarA.a(f10) : f10 / l0());
    }

    default long j(long j10) {
        if (j10 != 9205357640488583168L) {
            return ge.G(h0(Float.intBitsToFloat((int) (j10 >> 32))), h0(Float.intBitsToFloat((int) (j10 & 4294967295L))));
        }
        return 9205357640488583168L;
    }

    float l0();

    default float m(long j10) {
        float fD;
        float fL0;
        if (!q.a(p.c(j10), 4294967296L)) {
            j.b("Only Sp can convert to Px");
        }
        float[] fArr = u3.b.f58260a;
        if (l0() >= 1.03f) {
            u3.a aVarA = u3.b.a(l0());
            fD = p.d(j10);
            if (aVarA != null) {
                return aVarA.b(fD);
            }
            fL0 = l0();
        } else {
            fD = p.d(j10);
            fL0 = l0();
        }
        return fL0 * fD;
    }

    default float o0(float f10) {
        return getDensity() * f10;
    }

    default long v(float f10) {
        return i(h0(f10));
    }
}

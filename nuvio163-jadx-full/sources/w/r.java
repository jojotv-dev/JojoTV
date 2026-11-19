package w;

/* JADX INFO: loaded from: classes.dex */
public final class r implements v {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final float f61381b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final float f61382c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final float f61383e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final float f61384i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public final float f61385m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public final float f61386n;

    public r(float f10, float f11, float f12, float f13) {
        int iH;
        this.f61381b = f10;
        this.f61382c = f11;
        this.f61383e = f12;
        this.f61384i = f13;
        if (!((Float.isNaN(f10) || Float.isNaN(f11) || Float.isNaN(f12) || Float.isNaN(f13)) ? false : true)) {
            r0.a("Parameters to CubicBezierEasing cannot be NaN. Actual parameters are: " + f10 + ", " + f11 + ", " + f12 + ", " + f13 + '.');
        }
        float[] fArr = new float[5];
        float f14 = (f11 - 0.0f) * 3.0f;
        float f15 = (f13 - f11) * 3.0f;
        float f16 = (1.0f - f13) * 3.0f;
        double d10 = f14;
        double d11 = f15;
        double d12 = f16;
        double d13 = d11 * 2.0d;
        double d14 = (d10 - d13) + d12;
        if (d14 == 0.0d) {
            iH = d11 == d12 ? 0 : d2.j0.H((float) ((d13 - d12) / (d13 - (d12 * 2.0d))), fArr, 0);
        } else {
            double d15 = -Math.sqrt((d11 * d11) - (d12 * d10));
            double d16 = (-d10) + d11;
            int iH2 = d2.j0.H((float) ((-(d15 + d16)) / d14), fArr, 0);
            int iH3 = d2.j0.H((float) ((d15 - d16) / d14), fArr, iH2) + iH2;
            if (iH3 > 1) {
                float f17 = fArr[0];
                float f18 = fArr[1];
                if (f17 > f18) {
                    fArr[0] = f18;
                    fArr[1] = f17;
                } else if (f17 == f18) {
                    iH = iH3 - 1;
                }
                iH = iH3;
            } else {
                iH = iH3;
            }
        }
        float f19 = (f15 - f14) * 2.0f;
        int iH4 = d2.j0.H((-f19) / (((f16 - f15) * 2.0f) - f19), fArr, iH) + iH;
        float fMin = Math.min(0.0f, 1.0f);
        float fMax = Math.max(0.0f, 1.0f);
        for (int i10 = 0; i10 < iH4; i10++) {
            float f20 = fArr[i10];
            float f21 = (((((((((f11 - f13) * 3.0f) + 1.0f) - 0.0f) * f20) + (((f13 - (f11 * 2.0f)) + 0.0f) * 3.0f)) * f20) + f14) * f20) + 0.0f;
            fMin = Math.min(fMin, f21);
            fMax = Math.max(fMax, f21);
        }
        long jFloatToRawIntBits = (((long) Float.floatToRawIntBits(fMin)) << 32) | (((long) Float.floatToRawIntBits(fMax)) & 4294967295L);
        this.f61385m = Float.intBitsToFloat((int) (jFloatToRawIntBits >> 32));
        this.f61386n = Float.intBitsToFloat((int) (jFloatToRawIntBits & 4294967295L));
    }

    /* JADX WARN: Code duplicated, block: B:24:0x0092 A[PHI: r3
  0x0092: PHI (r3v28 float) = (r3v5 float), (r3v16 float), (r3v21 float), (r3v32 float), (r3v37 float) binds: [B:128:0x0236, B:117:0x0206, B:92:0x01bb, B:47:0x00e5, B:22:0x008e] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:69:0x0162 A[PHI: r12
  0x0162: PHI (r12v41 float) = (r12v25 float), (r12v36 float) binds: [B:68:0x0160, B:81:0x0191] A[DONT_GENERATE, DONT_INLINE]] */
    @Override // w.v
    public final float c(float f10) {
        float f11;
        if (f10 <= 0.0f || f10 >= 1.0f) {
            return f10;
        }
        float fMax = Math.max(f10, 1.1920929E-7f);
        float f12 = 0.0f - fMax;
        float f13 = this.f61381b;
        float f14 = f13 - fMax;
        float f15 = this.f61383e;
        float f16 = f15 - fMax;
        double d10 = f12;
        float f17 = 0.0f;
        double d11 = ((d10 - (((double) f14) * 2.0d)) + ((double) f16)) * 3.0d;
        double d12 = ((double) (f14 - f12)) * 3.0d;
        double d13 = (((double) (f14 - f16)) * 3.0d) + ((double) (-f12)) + ((double) (1.0f - fMax));
        float f18 = Float.NaN;
        if (Math.abs(d13 - 0.0d) >= 1.0E-7d) {
            double d14 = d11 / d13;
            double d15 = d12 / d13;
            double d16 = d10 / d13;
            double d17 = ((d15 * 3.0d) - (d14 * d14)) / 9.0d;
            double d18 = ((d16 * 27.0d) + ((((2.0d * d14) * d14) * d14) - ((9.0d * d14) * d15))) / 54.0d;
            double d19 = d17 * d17 * d17;
            double d20 = (d18 * d18) + d19;
            double d21 = d14 / 3.0d;
            if (d20 < 0.0d) {
                double dSqrt = Math.sqrt(-d19);
                double d22 = (-d18) / dSqrt;
                if (d22 < -1.0d) {
                    d22 = -1.0d;
                }
                if (d22 > 1.0d) {
                    d22 = 1.0d;
                }
                double dAcos = Math.acos(d22);
                double dK = v3.b.k((float) dSqrt) * 2.0f;
                float fCos = (float) ((Math.cos(dAcos / 3.0d) * dK) - d21);
                float f19 = fCos < 0.0f ? 0.0f : fCos;
                if (f19 > 1.0f) {
                    f19 = 1.0f;
                }
                if (Math.abs(f19 - fCos) > 1.05E-6f) {
                    f19 = Float.NaN;
                }
                if (Float.isNaN(f19)) {
                    float fCos2 = (float) ((Math.cos((6.283185307179586d + dAcos) / 3.0d) * dK) - d21);
                    f19 = fCos2 < 0.0f ? 0.0f : fCos2;
                    if (f19 > 1.0f) {
                        f19 = 1.0f;
                    }
                    if (Math.abs(f19 - fCos2) > 1.05E-6f) {
                        f19 = Float.NaN;
                    }
                    if (Float.isNaN(f19)) {
                        float fCos3 = (float) ((Math.cos((dAcos + 12.566370614359172d) / 3.0d) * dK) - d21);
                        if (fCos3 >= 0.0f) {
                            f17 = fCos3;
                        }
                        f11 = f17 > 1.0f ? 1.0f : f17;
                        if (Math.abs(f11 - fCos3) <= 1.05E-6f) {
                            f18 = f11;
                        }
                    } else {
                        f18 = f19;
                    }
                } else {
                    f18 = f19;
                }
            } else if (d20 == 0.0d) {
                float f20 = -v3.b.k((float) d18);
                float f21 = (float) d21;
                float f22 = (f20 * 2.0f) - f21;
                float f23 = f22 < 0.0f ? 0.0f : f22;
                if (f23 > 1.0f) {
                    f23 = 1.0f;
                }
                if (Math.abs(f23 - f22) > 1.05E-6f) {
                    f23 = Float.NaN;
                }
                if (Float.isNaN(f23)) {
                    float f24 = (-f20) - f21;
                    if (f24 >= 0.0f) {
                        f17 = f24;
                    }
                    f11 = f17 > 1.0f ? 1.0f : f17;
                    if (Math.abs(f11 - f24) <= 1.05E-6f) {
                        f18 = f11;
                    }
                } else {
                    f18 = f23;
                }
            } else {
                double dSqrt2 = Math.sqrt(d20);
                float fK = (float) (((double) (v3.b.k((float) ((-d18) + dSqrt2)) - v3.b.k((float) (d18 + dSqrt2)))) - d21);
                if (fK >= 0.0f) {
                    f17 = fK;
                }
                f11 = f17 > 1.0f ? 1.0f : f17;
                if (Math.abs(f11 - fK) <= 1.05E-6f) {
                    f18 = f11;
                }
            }
        } else if (Math.abs(d11 - 0.0d) >= 1.0E-7d) {
            double dSqrt3 = Math.sqrt((d12 * d12) - ((4.0d * d11) * d10));
            double d23 = d11 * 2.0d;
            float f25 = (float) ((dSqrt3 - d12) / d23);
            float f26 = f25 < 0.0f ? 0.0f : f25;
            if (f26 > 1.0f) {
                f26 = 1.0f;
            }
            if (Math.abs(f26 - f25) > 1.05E-6f) {
                f26 = Float.NaN;
            }
            if (Float.isNaN(f26)) {
                float f27 = (float) (((-d12) - dSqrt3) / d23);
                if (f27 >= 0.0f) {
                    f17 = f27;
                }
                f11 = f17 > 1.0f ? 1.0f : f17;
                if (Math.abs(f11 - f27) <= 1.05E-6f) {
                    f18 = f11;
                }
            } else {
                f18 = f26;
            }
        } else if (Math.abs(d12 - 0.0d) >= 1.0E-7d) {
            float f28 = (float) ((-d10) / d12);
            if (f28 >= 0.0f) {
                f17 = f28;
            }
            f11 = f17 > 1.0f ? 1.0f : f17;
            if (Math.abs(f11 - f28) <= 1.05E-6f) {
                f18 = f11;
            }
        }
        boolean zIsNaN = Float.isNaN(f18);
        float f29 = this.f61384i;
        float f30 = this.f61382c;
        if (!zIsNaN) {
            float f31 = ((((((f30 - f29) + 0.33333334f) * f18) + (f29 - (2.0f * f30))) * f18) + f30) * 3.0f * f18;
            float f32 = this.f61385m;
            if (f31 < f32) {
                f31 = f32;
            }
            float f33 = this.f61386n;
            return f31 > f33 ? f33 : f31;
        }
        throw new IllegalArgumentException("The cubic curve with parameters (" + f13 + ", " + f30 + ", " + f15 + ", " + f29 + ") has no solution at " + f10);
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof r)) {
            return false;
        }
        r rVar = (r) obj;
        return this.f61381b == rVar.f61381b && this.f61382c == rVar.f61382c && this.f61383e == rVar.f61383e && this.f61384i == rVar.f61384i;
    }

    public final int hashCode() {
        return Float.hashCode(this.f61384i) + o2.a.b(this.f61383e, o2.a.b(this.f61382c, Float.hashCode(this.f61381b) * 31, 31), 31);
    }

    public final String toString() {
        StringBuilder sb2 = new StringBuilder("CubicBezierEasing(a=");
        sb2.append(this.f61381b);
        sb2.append(", b=");
        sb2.append(this.f61382c);
        sb2.append(", c=");
        sb2.append(this.f61383e);
        sb2.append(", d=");
        return o2.a.o(sb2, this.f61384i, ')');
    }
}

package b4;

/* JADX INFO: loaded from: classes3.dex */
public final class m {
    public static final m k;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final float f4334a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final float f4335b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final float f4336c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final float f4337d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final float f4338e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public final float f4339f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public final float[] f4340g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public final float f4341h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final float f4342i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public final float f4343j;

    static {
        float fP = (float) ((((double) b.p()) * 63.66197723675813d) / 100.0d);
        float[] fArr = b.f4306c;
        float f10 = fArr[0];
        float[][] fArr2 = b.f4304a;
        float[] fArr3 = fArr2[0];
        float f11 = fArr3[0] * f10;
        float f12 = fArr[1];
        float f13 = (fArr3[1] * f12) + f11;
        float f14 = fArr[2];
        float f15 = (fArr3[2] * f14) + f13;
        float[] fArr4 = fArr2[1];
        float f16 = (fArr4[2] * f14) + (fArr4[1] * f12) + (fArr4[0] * f10);
        float[] fArr5 = fArr2[2];
        float f17 = (f14 * fArr5[2]) + (f12 * fArr5[1]) + (f10 * fArr5[0]);
        float f18 = ((double) 1.0f) >= 0.9d ? 0.69f : 0.655f;
        float fExp = (1.0f - (((float) Math.exp(((-fP) - 42.0f) / 92.0f)) * 0.2777778f)) * 1.0f;
        double d10 = fExp;
        if (d10 > 1.0d) {
            fExp = 1.0f;
        } else if (d10 < 0.0d) {
            fExp = 0.0f;
        }
        float[] fArr6 = {(((100.0f / f15) * fExp) + 1.0f) - fExp, (((100.0f / f16) * fExp) + 1.0f) - fExp, (((100.0f / f17) * fExp) + 1.0f) - fExp};
        float f19 = 1.0f / ((5.0f * fP) + 1.0f);
        float f20 = f19 * f19 * f19 * f19;
        float f21 = 1.0f - f20;
        float fCbrt = (0.1f * f21 * f21 * ((float) Math.cbrt(((double) fP) * 5.0d))) + (f20 * fP);
        float fP2 = b.p() / fArr[1];
        double d11 = fP2;
        float fSqrt = ((float) Math.sqrt(d11)) + 1.48f;
        float fPow = 0.725f / ((float) Math.pow(d11, 0.2d));
        float[] fArr7 = {(float) Math.pow(((double) ((fArr6[0] * fCbrt) * f15)) / 100.0d, 0.42d), (float) Math.pow(((double) ((fArr6[1] * fCbrt) * f16)) / 100.0d, 0.42d), (float) Math.pow(((double) ((fArr6[2] * fCbrt) * f17)) / 100.0d, 0.42d)};
        float f22 = fArr7[0];
        float f23 = (f22 * 400.0f) / (f22 + 27.13f);
        float f24 = fArr7[1];
        float f25 = (f24 * 400.0f) / (f24 + 27.13f);
        float f26 = fArr7[2];
        float[] fArr8 = {f23, f25, (400.0f * f26) / (f26 + 27.13f)};
        k = new m(fP2, ((fArr8[2] * 0.05f) + (fArr8[0] * 2.0f) + fArr8[1]) * fPow, fPow, fPow, f18, 1.0f, fArr6, fCbrt, (float) Math.pow(fCbrt, 0.25d), fSqrt);
    }

    public m(float f10, float f11, float f12, float f13, float f14, float f15, float[] fArr, float f16, float f17, float f18) {
        this.f4339f = f10;
        this.f4334a = f11;
        this.f4335b = f12;
        this.f4336c = f13;
        this.f4337d = f14;
        this.f4338e = f15;
        this.f4340g = fArr;
        this.f4341h = f16;
        this.f4342i = f17;
        this.f4343j = f18;
    }
}

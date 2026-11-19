package a0;

/* JADX INFO: loaded from: classes.dex */
public final class g implements e {
    @Override // a0.e
    public final float calculateScrollDistance(float f10, float f11, float f12) {
        float fAbs = Math.abs((f11 + f10) - f10);
        float f13 = (0.3f * f12) - (0.0f * fAbs);
        float f14 = f12 - f13;
        if ((fAbs <= f12) && f14 < fAbs) {
            f13 = f12 - fAbs;
        }
        return f10 - f13;
    }
}

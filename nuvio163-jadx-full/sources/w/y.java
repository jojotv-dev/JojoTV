package w;

/* JADX INFO: loaded from: classes.dex */
public interface y extends j {
    @Override // w.j
    default b2 a(z1 z1Var) {
        return new g5.e(this);
    }

    default float b(float f10, float f11, float f12) {
        return d(e(f10, f11, f12), f10, f11, f12);
    }

    float c(long j10, float f10, float f11, float f12);

    float d(long j10, float f10, float f11, float f12);

    long e(float f10, float f11, float f12);
}

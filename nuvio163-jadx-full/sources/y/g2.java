package y;

/* JADX INFO: loaded from: classes.dex */
public final class g2 extends e2 {
    @Override // y.e2, y.c2
    public final void a(long j10, long j11) {
        if (!Float.isNaN(Float.NaN)) {
            this.f64351a.setZoom(Float.NaN);
        }
        if ((9223372034707292159L & j11) != 9205357640488583168L) {
            this.f64351a.show(Float.intBitsToFloat((int) (j10 >> 32)), Float.intBitsToFloat((int) (j10 & 4294967295L)), Float.intBitsToFloat((int) (j11 >> 32)), Float.intBitsToFloat((int) (j11 & 4294967295L)));
        } else {
            this.f64351a.show(Float.intBitsToFloat((int) (j10 >> 32)), Float.intBitsToFloat((int) (j10 & 4294967295L)));
        }
    }
}

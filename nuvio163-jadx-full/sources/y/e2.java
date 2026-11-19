package y;

import android.widget.Magnifier;

/* JADX INFO: loaded from: classes.dex */
public class e2 implements c2 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Magnifier f64351a;

    public e2(Magnifier magnifier) {
        this.f64351a = magnifier;
    }

    @Override // y.c2
    public void a(long j10, long j11) {
        this.f64351a.show(Float.intBitsToFloat((int) (j10 >> 32)), Float.intBitsToFloat((int) (j10 & 4294967295L)));
    }

    public final void b() {
        this.f64351a.dismiss();
    }

    public final long c() {
        return (((long) this.f64351a.getHeight()) & 4294967295L) | (((long) this.f64351a.getWidth()) << 32);
    }

    public final void d() {
        this.f64351a.update();
    }
}

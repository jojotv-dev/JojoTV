package k4;

import android.view.animation.Interpolator;

/* JADX INFO: loaded from: classes.dex */
public abstract class l1 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f27803a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public float f27804b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final Interpolator f27805c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final long f27806d;

    public l1(int i10, Interpolator interpolator, long j10) {
        this.f27803a = i10;
        this.f27805c = interpolator;
        this.f27806d = j10;
    }

    public float a() {
        return 1.0f;
    }

    public long b() {
        return this.f27806d;
    }

    public float c() {
        Interpolator interpolator = this.f27805c;
        return interpolator != null ? interpolator.getInterpolation(this.f27804b) : this.f27804b;
    }

    public int d() {
        return this.f27803a;
    }

    public void e(float f10) {
        this.f27804b = f10;
    }
}

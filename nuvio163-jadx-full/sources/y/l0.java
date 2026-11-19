package y;

import android.content.Context;
import android.os.Build;
import android.widget.EdgeEffect;

/* JADX INFO: loaded from: classes.dex */
public final class l0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Context f64430a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f64431b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public long f64432c = 0;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public EdgeEffect f64433d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public EdgeEffect f64434e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public EdgeEffect f64435f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public EdgeEffect f64436g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public EdgeEffect f64437h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public EdgeEffect f64438i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public EdgeEffect f64439j;
    public EdgeEffect k;

    public l0(Context context, int i10) {
        this.f64430a = context;
        this.f64431b = i10;
    }

    public static boolean f(EdgeEffect edgeEffect) {
        if (edgeEffect == null) {
            return false;
        }
        return !edgeEffect.isFinished();
    }

    public static boolean g(EdgeEffect edgeEffect) {
        if (edgeEffect == null) {
            return false;
        }
        return !((Build.VERSION.SDK_INT >= 31 ? k.b(edgeEffect) : 0.0f) == 0.0f);
    }

    public final EdgeEffect a(a0.e2 e2Var) {
        int i10 = Build.VERSION.SDK_INT;
        Context context = this.f64430a;
        EdgeEffect edgeEffectA = i10 >= 31 ? k.a(context) : new u0(context);
        edgeEffectA.setColor(this.f64431b);
        if (!t3.m.a(this.f64432c, 0L)) {
            if (e2Var == a0.e2.f100b) {
                long j10 = this.f64432c;
                edgeEffectA.setSize((int) (j10 >> 32), (int) (j10 & 4294967295L));
                return edgeEffectA;
            }
            long j11 = this.f64432c;
            edgeEffectA.setSize((int) (j11 & 4294967295L), (int) (j11 >> 32));
        }
        return edgeEffectA;
    }

    public final EdgeEffect b() {
        EdgeEffect edgeEffect = this.f64434e;
        if (edgeEffect != null) {
            return edgeEffect;
        }
        EdgeEffect edgeEffectA = a(a0.e2.f100b);
        this.f64434e = edgeEffectA;
        return edgeEffectA;
    }

    public final EdgeEffect c() {
        EdgeEffect edgeEffect = this.f64435f;
        if (edgeEffect != null) {
            return edgeEffect;
        }
        EdgeEffect edgeEffectA = a(a0.e2.f101c);
        this.f64435f = edgeEffectA;
        return edgeEffectA;
    }

    public final EdgeEffect d() {
        EdgeEffect edgeEffect = this.f64436g;
        if (edgeEffect != null) {
            return edgeEffect;
        }
        EdgeEffect edgeEffectA = a(a0.e2.f101c);
        this.f64436g = edgeEffectA;
        return edgeEffectA;
    }

    public final EdgeEffect e() {
        EdgeEffect edgeEffect = this.f64433d;
        if (edgeEffect != null) {
            return edgeEffect;
        }
        EdgeEffect edgeEffectA = a(a0.e2.f100b);
        this.f64433d = edgeEffectA;
        return edgeEffectA;
    }
}

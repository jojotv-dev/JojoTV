package y;

import android.view.ViewConfiguration;

/* JADX INFO: loaded from: classes.dex */
public abstract class k0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final float f64410a = ViewConfiguration.getScrollFriction();

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final double f64411b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final double f64412c;

    static {
        double dLog = Math.log(0.78d) / Math.log(0.9d);
        f64411b = dLog;
        f64412c = dLog - 1.0d;
    }
}

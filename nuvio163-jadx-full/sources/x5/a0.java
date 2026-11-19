package x5;

import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import o.r2;

/* JADX INFO: loaded from: classes3.dex */
public abstract class a0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final b0 f63428a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final r2 f63429b;

    static {
        if (Build.VERSION.SDK_INT >= 29) {
            f63428a = new c0();
        } else {
            f63428a = new b0();
        }
        f63429b = new r2(6, "translationAlpha", Float.class);
        new r2(7, "clipBounds", Rect.class);
    }

    public static void a(View view, int i10, int i11, int i12, int i13) {
        f63428a.l(view, i10, i11, i12, i13);
    }

    public static void b(int i10, View view) {
        f63428a.k(i10, view);
    }
}

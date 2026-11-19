package i3;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.StaticLayout;

/* JADX INFO: loaded from: classes.dex */
public abstract class e {
    public static boolean a(Canvas canvas, Path path) {
        return canvas.clipOutPath(path);
    }

    public static boolean b(Canvas canvas, float f10, float f11, float f12, float f13) {
        return canvas.clipOutRect(f10, f11, f12, f13);
    }

    public static boolean c(Canvas canvas, int i10, int i11, int i12, int i13) {
        return canvas.clipOutRect(i10, i11, i12, i13);
    }

    public static boolean d(Canvas canvas, Rect rect) {
        return canvas.clipOutRect(rect);
    }

    public static boolean e(Canvas canvas, RectF rectF) {
        return canvas.clipOutRect(rectF);
    }

    public static final void f(StaticLayout.Builder builder, int i10) {
        builder.setJustificationMode(i10);
    }
}

package d2;

import android.graphics.PathMeasure;

/* JADX INFO: loaded from: classes.dex */
public final class n {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final PathMeasure f11749a;

    public n(PathMeasure pathMeasure) {
        this.f11749a = pathMeasure;
    }

    public final boolean a(float f10, float f11, l lVar) {
        if (lVar == null) {
            throw new UnsupportedOperationException("Unable to obtain android.graphics.Path");
        }
        return this.f11749a.getSegment(f10, f11, lVar.f11742a, true);
    }
}

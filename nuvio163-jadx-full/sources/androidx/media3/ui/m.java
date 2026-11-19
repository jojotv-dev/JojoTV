package androidx.media3.ui;

import android.text.GraphemeClusterSegmentFinder;
import android.text.TextPaint;
import android.window.SurfaceSyncGroup;

/* JADX INFO: loaded from: classes3.dex */
public abstract /* synthetic */ class m {
    public static /* synthetic */ GraphemeClusterSegmentFinder a(CharSequence charSequence, TextPaint textPaint) {
        return new GraphemeClusterSegmentFinder(charSequence, textPaint);
    }

    public static /* synthetic */ SurfaceSyncGroup b() {
        return new SurfaceSyncGroup("exo-sync-b-334901521");
    }

    public static /* synthetic */ void c() {
    }
}

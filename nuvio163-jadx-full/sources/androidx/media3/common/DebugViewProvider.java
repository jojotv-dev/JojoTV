package androidx.media3.common;

import a3.c;
import android.view.SurfaceView;
import androidx.media3.common.util.UnstableApi;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public interface DebugViewProvider {
    public static final DebugViewProvider NONE = new c(5);

    /* JADX INFO: Access modifiers changed from: private */
    static /* synthetic */ SurfaceView lambda$static$0(int i10, int i11) {
        return null;
    }

    SurfaceView getDebugPreviewSurfaceView(int i10, int i11);
}

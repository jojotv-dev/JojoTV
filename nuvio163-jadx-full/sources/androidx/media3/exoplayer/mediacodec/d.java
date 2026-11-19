package androidx.media3.exoplayer.mediacodec;

import android.view.SurfaceControl;
import android.view.WindowInsets;

/* JADX INFO: loaded from: classes3.dex */
public abstract /* synthetic */ class d {
    public static /* synthetic */ android.media.MediaCodecInfo.VideoCapabilities.PerformancePoint a() {
        return new android.media.MediaCodecInfo.VideoCapabilities.PerformancePoint(1280, 720, 60);
    }

    public static /* synthetic */ SurfaceControl.Transaction b() {
        return new SurfaceControl.Transaction();
    }

    public static /* synthetic */ WindowInsets.Builder c() {
        return new WindowInsets.Builder();
    }

    public static /* synthetic */ WindowInsets.Builder d(WindowInsets windowInsets) {
        return new WindowInsets.Builder(windowInsets);
    }

    public static /* synthetic */ void e() {
    }
}

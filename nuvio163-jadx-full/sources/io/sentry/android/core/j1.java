package io.sentry.android.core;

import io.sentry.u5;
import java.util.concurrent.CopyOnWriteArraySet;

/* JADX INFO: loaded from: classes.dex */
public final class j1 extends u5 {
    @Override // io.sentry.u5
    public final void b(boolean z2) {
        super.b(z2);
        if (z2) {
            a("android.webkit.WebView");
            a("android.widget.VideoView");
            a("androidx.camera.view.PreviewView");
            a("androidx.media3.ui.PlayerView");
            a("com.google.android.exoplayer2.ui.PlayerView");
            a("com.google.android.exoplayer2.ui.StyledPlayerView");
            return;
        }
        CopyOnWriteArraySet copyOnWriteArraySet = this.f26229a;
        copyOnWriteArraySet.remove("android.webkit.WebView");
        copyOnWriteArraySet.remove("android.widget.VideoView");
        copyOnWriteArraySet.remove("androidx.camera.view.PreviewView");
        copyOnWriteArraySet.remove("androidx.media3.ui.PlayerView");
        copyOnWriteArraySet.remove("com.google.android.exoplayer2.ui.PlayerView");
        copyOnWriteArraySet.remove("com.google.android.exoplayer2.ui.StyledPlayerView");
    }

    @Override // io.sentry.u5
    public final void d() {
    }
}

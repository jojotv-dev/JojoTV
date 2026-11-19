package io.sentry.android.core.performance;

import android.view.Window;
import io.sentry.android.core.internal.gestures.j;

/* JADX INFO: loaded from: classes5.dex */
public final class i extends j {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final androidx.media3.exoplayer.audio.j f24933c;

    public i(Window.Callback callback, androidx.media3.exoplayer.audio.j jVar) {
        super(callback);
        this.f24933c = jVar;
    }

    @Override // io.sentry.android.core.internal.gestures.j, android.view.Window.Callback
    public final void onContentChanged() {
        super.onContentChanged();
        this.f24933c.run();
    }
}

package androidx.media3.exoplayer.audio;

import android.media.AudioTrack;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import io.sentry.android.core.m0;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class j implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3058b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Object f3059c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ Object f3060e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final /* synthetic */ Object f3061i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public final /* synthetic */ Object f3062m;

    public /* synthetic */ j(int i10, Object obj, Object obj2, Object obj3, Object obj4) {
        this.f3058b = i10;
        this.f3059c = obj;
        this.f3060e = obj2;
        this.f3061i = obj3;
        this.f3062m = obj4;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f3058b) {
            case 0:
                DefaultAudioSink.lambda$releaseAudioTrackAsync$1((AudioTrack) this.f3059c, (AudioSink.Listener) this.f3060e, (Handler) this.f3061i, (AudioSink.AudioTrackConfig) this.f3062m);
                break;
            default:
                Window window = (Window) this.f3059c;
                Window.Callback callback = (Window.Callback) this.f3060e;
                Runnable runnable = (Runnable) this.f3061i;
                m0 m0Var = (m0) this.f3062m;
                View viewPeekDecorView = window.peekDecorView();
                if (viewPeekDecorView != null) {
                    window.setCallback(callback);
                    io.sentry.android.core.internal.util.j.b(viewPeekDecorView, runnable, m0Var);
                }
                break;
        }
    }
}

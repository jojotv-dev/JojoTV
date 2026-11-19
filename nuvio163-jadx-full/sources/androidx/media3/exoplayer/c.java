package androidx.media3.exoplayer;

import android.media.AudioManager;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class c implements AudioManager.OnAudioFocusChangeListener {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ AudioFocusManager f3071b;

    public /* synthetic */ c(AudioFocusManager audioFocusManager) {
        this.f3071b = audioFocusManager;
    }

    @Override // android.media.AudioManager.OnAudioFocusChangeListener
    public final void onAudioFocusChange(int i10) {
        this.f3071b.handlePlatformAudioFocusChange(i10);
    }
}

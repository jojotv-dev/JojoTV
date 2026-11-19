package androidx.media3.exoplayer;

import androidx.media3.common.Player;
import androidx.media3.common.util.ListenerSet;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class a0 implements ListenerSet.Event {
    @Override // androidx.media3.common.util.ListenerSet.Event
    public void invoke(Object obj) {
        ExoPlayerImpl.lambda$release$6((Player.Listener) obj);
    }
}

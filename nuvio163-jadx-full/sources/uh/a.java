package uh;

import androidx.media3.exoplayer.upstream.CmcdData;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/* JADX INFO: loaded from: classes5.dex */
public final class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public volatile int f59951a;

    static {
        AtomicIntegerFieldUpdater.newUpdater(a.class, CmcdData.OBJECT_TYPE_AUDIO_ONLY);
    }

    public final String toString() {
        return String.valueOf(this.f59951a != 0);
    }
}

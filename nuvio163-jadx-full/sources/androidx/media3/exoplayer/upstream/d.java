package androidx.media3.exoplayer.upstream;

import androidx.media3.common.util.Consumer;
import java.util.concurrent.ExecutorService;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class d implements Consumer {
    @Override // androidx.media3.common.util.Consumer, ih.b
    public final void accept(Object obj) {
        ((ExecutorService) obj).shutdown();
    }
}

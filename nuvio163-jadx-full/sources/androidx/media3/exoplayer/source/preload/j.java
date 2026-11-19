package androidx.media3.exoplayer.source.preload;

import androidx.media3.common.util.Consumer;
import java.io.IOException;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class j implements Consumer {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3307b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ PreCacheHelper.DownloadCallback f3308c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ IOException f3309e;

    public /* synthetic */ j(PreCacheHelper.DownloadCallback downloadCallback, IOException iOException, int i10) {
        this.f3307b = i10;
        this.f3308c = downloadCallback;
        this.f3309e = iOException;
    }

    @Override // androidx.media3.common.util.Consumer, ih.b
    public final void accept(Object obj) {
        switch (this.f3307b) {
            case 0:
                this.f3308c.lambda$onDownloadStopped$2(this.f3309e, (PreCacheHelper.Listener) obj);
                break;
            default:
                this.f3308c.lambda$onPrepareError$1(this.f3309e, (PreCacheHelper.Listener) obj);
                break;
        }
    }
}

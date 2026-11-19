package androidx.media3.exoplayer.offline;

import java.io.IOException;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class g implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3218b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Object f3219c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ Object f3220e;

    public /* synthetic */ g(Object obj, Object obj2, int i10) {
        this.f3218b = i10;
        this.f3219c = obj;
        this.f3220e = obj2;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f3218b) {
            case 0:
                ((DownloadService.DownloadManagerHelper) this.f3219c).lambda$attachService$0((DownloadService) this.f3220e);
                break;
            case 1:
                ((DownloadHelper) this.f3219c).lambda$onMediaPreparationFailed$3((IOException) this.f3220e);
                break;
            default:
                ((DownloadHelper) this.f3219c).lambda$prepare$1((DownloadHelper.Callback) this.f3220e);
                break;
        }
    }
}

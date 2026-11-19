package androidx.media3.exoplayer.offline;

import android.os.Handler;
import android.os.Message;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class d implements Handler.Callback {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3215b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Object f3216c;

    public /* synthetic */ d(Object obj, int i10) {
        this.f3215b = i10;
        this.f3216c = obj;
    }

    @Override // android.os.Handler.Callback
    public final boolean handleMessage(Message message) {
        switch (this.f3215b) {
            case 0:
                return ((DownloadHelper.MediaPreparer) this.f3216c).handleDownloadHelperCallbackMessage(message);
            default:
                return ((DownloadManager) this.f3216c).handleMainMessage(message);
        }
    }
}

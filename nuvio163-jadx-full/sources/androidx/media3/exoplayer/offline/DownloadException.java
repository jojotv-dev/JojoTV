package androidx.media3.exoplayer.offline;

import androidx.media3.common.util.UnstableApi;
import java.io.IOException;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public final class DownloadException extends IOException {
    public DownloadException(String str) {
        super(str);
    }

    public DownloadException(Throwable th2) {
        super(th2);
    }
}

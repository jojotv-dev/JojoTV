package androidx.media3.exoplayer.offline;

import androidx.media3.common.util.UnstableApi;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public interface Downloader {

    public interface ProgressListener {
        void onProgress(long j10, long j11, float f10);
    }

    void cancel();

    void download(ProgressListener progressListener);

    void remove();
}

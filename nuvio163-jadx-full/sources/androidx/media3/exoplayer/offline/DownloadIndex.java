package androidx.media3.exoplayer.offline;

import androidx.media3.common.util.UnstableApi;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public interface DownloadIndex {
    Download getDownload(String str);

    DownloadCursor getDownloads(int... iArr);
}

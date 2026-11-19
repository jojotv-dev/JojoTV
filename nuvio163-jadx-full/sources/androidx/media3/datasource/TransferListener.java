package androidx.media3.datasource;

import androidx.media3.common.util.UnstableApi;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public interface TransferListener {
    void onBytesTransferred(DataSource dataSource, DataSpec dataSpec, boolean z2, int i10);

    void onTransferEnd(DataSource dataSource, DataSpec dataSpec, boolean z2);

    void onTransferInitializing(DataSource dataSource, DataSpec dataSpec, boolean z2);

    void onTransferStart(DataSource dataSource, DataSpec dataSpec, boolean z2);
}

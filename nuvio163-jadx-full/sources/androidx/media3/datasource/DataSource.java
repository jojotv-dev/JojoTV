package androidx.media3.datasource;

import android.net.Uri;
import androidx.media3.common.DataReader;
import androidx.media3.common.util.UnstableApi;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public interface DataSource extends DataReader {

    /* JADX INFO: loaded from: classes3.dex */
    public interface Factory {
        @UnstableApi
        DataSource createDataSource();
    }

    @UnstableApi
    void addTransferListener(TransferListener transferListener);

    @UnstableApi
    void close();

    @UnstableApi
    default Map<String, List<String>> getResponseHeaders() {
        return Collections.EMPTY_MAP;
    }

    @UnstableApi
    Uri getUri();

    @UnstableApi
    long open(DataSpec dataSpec);
}

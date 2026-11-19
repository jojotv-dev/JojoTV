package androidx.media3.common;

import androidx.media3.common.util.UnstableApi;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public interface ByteBufferDataReader extends DataReader {
    int read(ByteBuffer byteBuffer, int i10);

    boolean supportsByteBufferRead();
}

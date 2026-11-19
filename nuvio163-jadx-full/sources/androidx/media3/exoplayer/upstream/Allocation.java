package androidx.media3.exoplayer.upstream;

import androidx.media3.common.util.UnstableApi;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: classes.dex */
@UnstableApi
public final class Allocation {
    public final ByteBuffer buffer;
    public final byte[] data;
    public long nativeHandle;
    public final int offset;

    public Allocation(byte[] bArr, int i10) {
        this.data = bArr;
        this.buffer = null;
        this.offset = i10;
        this.nativeHandle = 0L;
    }

    public Allocation(ByteBuffer byteBuffer, int i10) {
        this.data = null;
        this.buffer = byteBuffer;
        this.offset = i10;
        this.nativeHandle = 0L;
    }

    public Allocation(ByteBuffer byteBuffer, int i10, long j10) {
        this.data = null;
        this.buffer = byteBuffer;
        this.offset = i10;
        this.nativeHandle = j10;
    }
}

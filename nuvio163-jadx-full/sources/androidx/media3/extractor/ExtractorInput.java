package androidx.media3.extractor;

import androidx.media3.common.DataReader;
import androidx.media3.common.util.UnstableApi;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public interface ExtractorInput extends DataReader {
    void advancePeekPosition(int i10);

    boolean advancePeekPosition(int i10, boolean z2);

    long getLength();

    long getPeekPosition();

    long getPosition();

    int peek(byte[] bArr, int i10, int i11);

    void peekFully(byte[] bArr, int i10, int i11);

    boolean peekFully(byte[] bArr, int i10, int i11, boolean z2);

    @Override // androidx.media3.common.DataReader
    int read(byte[] bArr, int i10, int i11);

    void readFully(byte[] bArr, int i10, int i11);

    boolean readFully(byte[] bArr, int i10, int i11, boolean z2);

    void resetPeekPosition();

    <E extends Throwable> void setRetryPosition(long j10, E e10);

    int skip(int i10);

    void skipFully(int i10);

    boolean skipFully(int i10, boolean z2);
}

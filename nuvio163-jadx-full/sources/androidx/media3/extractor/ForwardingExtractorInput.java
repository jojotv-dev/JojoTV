package androidx.media3.extractor;

import androidx.media3.common.ByteBufferDataReader;
import androidx.media3.common.util.UnstableApi;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public class ForwardingExtractorInput implements ExtractorInput, ByteBufferDataReader {
    private final ExtractorInput input;

    public ForwardingExtractorInput(ExtractorInput extractorInput) {
        this.input = extractorInput;
    }

    @Override // androidx.media3.extractor.ExtractorInput
    public boolean advancePeekPosition(int i10, boolean z2) {
        return this.input.advancePeekPosition(i10, z2);
    }

    @Override // androidx.media3.extractor.ExtractorInput
    public long getLength() {
        return this.input.getLength();
    }

    @Override // androidx.media3.extractor.ExtractorInput
    public long getPeekPosition() {
        return this.input.getPeekPosition();
    }

    @Override // androidx.media3.extractor.ExtractorInput
    public long getPosition() {
        return this.input.getPosition();
    }

    @Override // androidx.media3.extractor.ExtractorInput
    public int peek(byte[] bArr, int i10, int i11) {
        return this.input.peek(bArr, i10, i11);
    }

    @Override // androidx.media3.extractor.ExtractorInput
    public boolean peekFully(byte[] bArr, int i10, int i11, boolean z2) {
        return this.input.peekFully(bArr, i10, i11, z2);
    }

    @Override // androidx.media3.extractor.ExtractorInput, androidx.media3.common.DataReader
    public int read(byte[] bArr, int i10, int i11) {
        return this.input.read(bArr, i10, i11);
    }

    @Override // androidx.media3.extractor.ExtractorInput
    public boolean readFully(byte[] bArr, int i10, int i11, boolean z2) {
        return this.input.readFully(bArr, i10, i11, z2);
    }

    @Override // androidx.media3.extractor.ExtractorInput
    public void resetPeekPosition() {
        this.input.resetPeekPosition();
    }

    @Override // androidx.media3.extractor.ExtractorInput
    public <E extends Throwable> void setRetryPosition(long j10, E e10) {
        this.input.setRetryPosition(j10, e10);
    }

    @Override // androidx.media3.extractor.ExtractorInput
    public int skip(int i10) {
        return this.input.skip(i10);
    }

    @Override // androidx.media3.extractor.ExtractorInput
    public boolean skipFully(int i10, boolean z2) {
        return this.input.skipFully(i10, z2);
    }

    @Override // androidx.media3.common.ByteBufferDataReader
    public boolean supportsByteBufferRead() {
        ExtractorInput extractorInput = this.input;
        return (extractorInput instanceof ByteBufferDataReader) && ((ByteBufferDataReader) extractorInput).supportsByteBufferRead();
    }

    @Override // androidx.media3.extractor.ExtractorInput
    public void advancePeekPosition(int i10) {
        this.input.advancePeekPosition(i10);
    }

    @Override // androidx.media3.extractor.ExtractorInput
    public void peekFully(byte[] bArr, int i10, int i11) {
        this.input.peekFully(bArr, i10, i11);
    }

    @Override // androidx.media3.common.ByteBufferDataReader
    public int read(ByteBuffer byteBuffer, int i10) {
        if (supportsByteBufferRead()) {
            return ((ByteBufferDataReader) this.input).read(byteBuffer, i10);
        }
        throw new UnsupportedOperationException();
    }

    @Override // androidx.media3.extractor.ExtractorInput
    public void readFully(byte[] bArr, int i10, int i11) {
        this.input.readFully(bArr, i10, i11);
    }

    @Override // androidx.media3.extractor.ExtractorInput
    public void skipFully(int i10) {
        this.input.skipFully(i10);
    }
}

package org.jsoup.internal;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.jsoup.helper.Validate;

/* JADX INFO: loaded from: classes5.dex */
class SimpleBufferedInput extends FilterInputStream {
    static final SoftPool<byte[]> BufferPool = new SoftPool<>(new e(0));
    static final int BufferSize = 8192;
    private int bufLength;
    private int bufMark;
    private int bufPos;
    private byte[] byteBuf;
    private int capRemaining;
    private boolean inReadFully;

    public SimpleBufferedInput(InputStream inputStream) {
        super(inputStream);
        this.capRemaining = Integer.MAX_VALUE;
        this.bufMark = -1;
        this.inReadFully = false;
        if (inputStream == null) {
            this.inReadFully = true;
        }
    }

    private void compact() {
        int i10;
        byte[] bArr = this.byteBuf;
        if (bArr == null || (i10 = this.bufPos) == 0) {
            return;
        }
        int i11 = this.bufMark;
        if (i11 >= 0) {
            i10 = i11;
        }
        if (i10 <= 0) {
            return;
        }
        int i12 = this.bufLength - i10;
        if (i12 > 0) {
            System.arraycopy(bArr, i10, bArr, 0, i12);
        }
        this.bufLength = i12;
        this.bufPos -= i10;
        int i13 = this.bufMark;
        if (i13 >= 0) {
            this.bufMark = i13 - i10;
        }
    }

    private void fill() throws IOException {
        int iMin;
        if (this.inReadFully) {
            return;
        }
        if (this.byteBuf == null) {
            this.byteBuf = BufferPool.borrow();
        }
        compact();
        int i10 = this.bufPos;
        this.bufLength = i10;
        int iMin2 = Math.min(this.byteBuf.length - i10, this.capRemaining);
        if (iMin2 <= 0) {
            return;
        }
        int i11 = ((FilterInputStream) this).in.read(this.byteBuf, this.bufPos, iMin2);
        if (i11 > 0) {
            this.bufLength = this.bufPos + i11;
            this.capRemaining -= i11;
            while (this.byteBuf.length - this.bufLength > 0 && this.capRemaining > 0 && ((FilterInputStream) this).in.available() >= 1 && (iMin = Math.min(this.byteBuf.length - this.bufLength, this.capRemaining)) > 0 && (i11 = ((FilterInputStream) this).in.read(this.byteBuf, this.bufLength, iMin)) > 0) {
                this.bufLength += i11;
                this.capRemaining -= i11;
            }
        }
        if (i11 == -1) {
            this.inReadFully = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ byte[] lambda$static$0() {
        return new byte[8192];
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int available() {
        int i10 = this.byteBuf != null ? this.bufLength - this.bufPos : 0;
        if (i10 > 0) {
            return i10;
        }
        if (this.inReadFully) {
            return 0;
        }
        return ((FilterInputStream) this).in.available();
    }

    public boolean baseReadFully() {
        return this.inReadFully;
    }

    public void capRemaining(int i10) {
        this.capRemaining = Math.max(0, i10);
    }

    public void clearMark() {
        this.bufMark = -1;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (((FilterInputStream) this).in != null) {
            super.close();
        }
        byte[] bArr = this.byteBuf;
        if (bArr == null) {
            return;
        }
        BufferPool.release(bArr);
        this.byteBuf = null;
    }

    public byte[] getBuf() {
        Validate.notNull(this.byteBuf);
        return this.byteBuf;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read() throws IOException {
        if (this.bufPos >= this.bufLength) {
            fill();
            if (this.bufPos >= this.bufLength) {
                return -1;
            }
        }
        byte[] buf = getBuf();
        int i10 = this.bufPos;
        this.bufPos = i10 + 1;
        return buf[i10] & 255;
    }

    public void resetFullyRead() {
        if (((FilterInputStream) this).in != null) {
            this.inReadFully = false;
        }
    }

    public void rewindToMark() throws IOException {
        int i10 = this.bufMark;
        if (i10 < 0) {
            throw new IOException("Resetting to invalid mark");
        }
        this.bufPos = i10;
    }

    public void setMark() {
        this.bufMark = this.bufPos;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] bArr, int i10, int i11) throws IOException {
        Validate.notNull(bArr);
        if (i10 < 0 || i11 < 0 || i11 > bArr.length - i10) {
            throw new IndexOutOfBoundsException();
        }
        if (i11 == 0) {
            return 0;
        }
        int i12 = this.bufLength - this.bufPos;
        if (i12 <= 0) {
            fill();
            i12 = this.bufLength - this.bufPos;
        }
        int iMin = Math.min(i12, i11);
        if (iMin <= 0) {
            return -1;
        }
        System.arraycopy(getBuf(), this.bufPos, bArr, i10, iMin);
        this.bufPos += iMin;
        return iMin;
    }
}

package pj;

import a0.i;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

/* JADX INFO: loaded from: classes5.dex */
public final class a extends InputStream {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final BufferedReader f45179b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final CharsetEncoder f45180c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final CharBuffer f45181e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final ByteBuffer f45182i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public CoderResult f45183m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public boolean f45184n;

    public a(BufferedReader bufferedReader) {
        CharsetEncoder charsetEncoderNewEncoder = Charset.forName("UTF-8").newEncoder();
        CodingErrorAction codingErrorAction = CodingErrorAction.REPLACE;
        CharsetEncoder charsetEncoderOnUnmappableCharacter = charsetEncoderNewEncoder.onMalformedInput(codingErrorAction).onUnmappableCharacter(codingErrorAction);
        this.f45179b = bufferedReader;
        this.f45180c = charsetEncoderOnUnmappableCharacter;
        CharBuffer charBufferAllocate = CharBuffer.allocate(1024);
        this.f45181e = charBufferAllocate;
        charBufferAllocate.flip();
        ByteBuffer byteBufferAllocate = ByteBuffer.allocate(128);
        this.f45182i = byteBufferAllocate;
        byteBufferAllocate.flip();
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public final void close() throws IOException {
        this.f45179b.close();
    }

    public final void g() throws IOException {
        CoderResult coderResult;
        boolean z2 = this.f45184n;
        CharBuffer charBuffer = this.f45181e;
        if (!z2 && ((coderResult = this.f45183m) == null || coderResult.isUnderflow())) {
            charBuffer.compact();
            int iPosition = charBuffer.position();
            int i10 = this.f45179b.read(charBuffer.array(), iPosition, charBuffer.remaining());
            if (i10 == -1) {
                this.f45184n = true;
            } else {
                charBuffer.position(iPosition + i10);
            }
            charBuffer.flip();
        }
        ByteBuffer byteBuffer = this.f45182i;
        byteBuffer.compact();
        this.f45183m = this.f45180c.encode(charBuffer, byteBuffer, this.f45184n);
        byteBuffer.flip();
    }

    @Override // java.io.InputStream
    public final int read(byte[] bArr, int i10, int i11) throws IOException {
        if (bArr == null) {
            throw new NullPointerException("array");
        }
        if (i11 < 0 || i10 < 0 || i10 + i11 > bArr.length) {
            StringBuilder sb2 = new StringBuilder("Array Size=");
            i.v(bArr.length, i10, ", offset=", ", length=", sb2);
            sb2.append(i11);
            throw new IndexOutOfBoundsException(sb2.toString());
        }
        int i12 = 0;
        if (i11 == 0) {
            return 0;
        }
        while (i11 > 0) {
            ByteBuffer byteBuffer = this.f45182i;
            if (!byteBuffer.hasRemaining()) {
                g();
                if (this.f45184n && !byteBuffer.hasRemaining()) {
                    break;
                }
            } else {
                int iMin = Math.min(byteBuffer.remaining(), i11);
                byteBuffer.get(bArr, i10, iMin);
                i10 += iMin;
                i11 -= iMin;
                i12 += iMin;
            }
        }
        if (i12 == 0 && this.f45184n) {
            return -1;
        }
        return i12;
    }

    @Override // java.io.InputStream
    public final int read(byte[] bArr) {
        return read(bArr, 0, bArr.length);
    }

    @Override // java.io.InputStream
    public final int read() throws IOException {
        while (true) {
            ByteBuffer byteBuffer = this.f45182i;
            if (byteBuffer.hasRemaining()) {
                return byteBuffer.get() & 255;
            }
            g();
            if (this.f45184n && !byteBuffer.hasRemaining()) {
                return -1;
            }
        }
    }
}

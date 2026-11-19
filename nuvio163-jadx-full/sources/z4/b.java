package z4;

import android.util.Log;
import dc.d1;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

/* JADX INFO: loaded from: classes.dex */
public class b extends InputStream implements DataInput {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final DataInputStream f65481b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f65482c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public ByteOrder f65483e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public byte[] f65484i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public final int f65485m;

    public b(byte[] bArr) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bArr);
        ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
        this(byteArrayInputStream, 0);
        this.f65485m = bArr.length;
    }

    @Override // java.io.InputStream
    public final int available() {
        return this.f65481b.available();
    }

    public final void g(int i10) throws IOException {
        int i11 = 0;
        while (i11 < i10) {
            int i12 = i10 - i11;
            DataInputStream dataInputStream = this.f65481b;
            int iSkip = (int) dataInputStream.skip(i12);
            if (iSkip <= 0) {
                if (this.f65484i == null) {
                    this.f65484i = new byte[8192];
                }
                iSkip = dataInputStream.read(this.f65484i, 0, Math.min(8192, i12));
                if (iSkip == -1) {
                    throw new EOFException(d1.f("Reached EOF while skipping ", i10, " bytes."));
                }
            }
            i11 += iSkip;
        }
        this.f65482c += i11;
    }

    @Override // java.io.InputStream
    public final void mark(int i10) {
        throw new UnsupportedOperationException("Mark is currently unsupported");
    }

    @Override // java.io.InputStream
    public final int read() {
        this.f65482c++;
        return this.f65481b.read();
    }

    @Override // java.io.DataInput
    public final boolean readBoolean() {
        this.f65482c++;
        return this.f65481b.readBoolean();
    }

    @Override // java.io.DataInput
    public final byte readByte() throws IOException {
        this.f65482c++;
        int i10 = this.f65481b.read();
        if (i10 >= 0) {
            return (byte) i10;
        }
        throw new EOFException();
    }

    @Override // java.io.DataInput
    public final char readChar() {
        this.f65482c += 2;
        return this.f65481b.readChar();
    }

    @Override // java.io.DataInput
    public final double readDouble() {
        return Double.longBitsToDouble(readLong());
    }

    @Override // java.io.DataInput
    public final float readFloat() {
        return Float.intBitsToFloat(readInt());
    }

    @Override // java.io.DataInput
    public final void readFully(byte[] bArr, int i10, int i11) throws IOException {
        this.f65482c += i11;
        this.f65481b.readFully(bArr, i10, i11);
    }

    @Override // java.io.DataInput
    public final int readInt() throws IOException {
        this.f65482c += 4;
        DataInputStream dataInputStream = this.f65481b;
        int i10 = dataInputStream.read();
        int i11 = dataInputStream.read();
        int i12 = dataInputStream.read();
        int i13 = dataInputStream.read();
        if ((i10 | i11 | i12 | i13) < 0) {
            throw new EOFException();
        }
        ByteOrder byteOrder = this.f65483e;
        if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
            return (i13 << 24) + (i12 << 16) + (i11 << 8) + i10;
        }
        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            return (i10 << 24) + (i11 << 16) + (i12 << 8) + i13;
        }
        throw new IOException("Invalid byte order: " + this.f65483e);
    }

    @Override // java.io.DataInput
    public final String readLine() {
        Log.d("ExifInterface", "Currently unsupported");
        return null;
    }

    @Override // java.io.DataInput
    public final long readLong() throws IOException {
        long j10;
        long j11;
        this.f65482c += 8;
        DataInputStream dataInputStream = this.f65481b;
        int i10 = dataInputStream.read();
        int i11 = dataInputStream.read();
        int i12 = dataInputStream.read();
        int i13 = dataInputStream.read();
        int i14 = dataInputStream.read();
        int i15 = dataInputStream.read();
        int i16 = dataInputStream.read();
        int i17 = dataInputStream.read();
        if ((i10 | i11 | i12 | i13 | i14 | i15 | i16 | i17) < 0) {
            throw new EOFException();
        }
        ByteOrder byteOrder = this.f65483e;
        if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
            j10 = (((long) i17) << 56) + (((long) i16) << 48) + (((long) i15) << 40) + (((long) i14) << 32) + (((long) i13) << 24) + (((long) i12) << 16) + (((long) i11) << 8);
            j11 = i10;
        } else {
            if (byteOrder != ByteOrder.BIG_ENDIAN) {
                throw new IOException("Invalid byte order: " + this.f65483e);
            }
            j10 = (((long) i10) << 56) + (((long) i11) << 48) + (((long) i12) << 40) + (((long) i13) << 32) + (((long) i14) << 24) + (((long) i15) << 16) + (((long) i16) << 8);
            j11 = i17;
        }
        return j10 + j11;
    }

    @Override // java.io.DataInput
    public final short readShort() throws IOException {
        this.f65482c += 2;
        DataInputStream dataInputStream = this.f65481b;
        int i10 = dataInputStream.read();
        int i11 = dataInputStream.read();
        if ((i10 | i11) < 0) {
            throw new EOFException();
        }
        ByteOrder byteOrder = this.f65483e;
        if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
            return (short) ((i11 << 8) + i10);
        }
        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            return (short) ((i10 << 8) + i11);
        }
        throw new IOException("Invalid byte order: " + this.f65483e);
    }

    @Override // java.io.DataInput
    public final String readUTF() {
        this.f65482c += 2;
        return this.f65481b.readUTF();
    }

    @Override // java.io.DataInput
    public final int readUnsignedByte() {
        this.f65482c++;
        return this.f65481b.readUnsignedByte();
    }

    @Override // java.io.DataInput
    public final int readUnsignedShort() throws IOException {
        this.f65482c += 2;
        DataInputStream dataInputStream = this.f65481b;
        int i10 = dataInputStream.read();
        int i11 = dataInputStream.read();
        if ((i10 | i11) < 0) {
            throw new EOFException();
        }
        ByteOrder byteOrder = this.f65483e;
        if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
            return (i11 << 8) + i10;
        }
        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            return (i10 << 8) + i11;
        }
        throw new IOException("Invalid byte order: " + this.f65483e);
    }

    @Override // java.io.InputStream
    public final void reset() {
        throw new UnsupportedOperationException("Reset is currently unsupported");
    }

    @Override // java.io.DataInput
    public final int skipBytes(int i10) {
        throw new UnsupportedOperationException("skipBytes is currently unsupported");
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public b(InputStream inputStream) {
        this(inputStream, 0);
        ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
    }

    @Override // java.io.InputStream
    public final int read(byte[] bArr, int i10, int i11) throws IOException {
        int i12 = this.f65481b.read(bArr, i10, i11);
        this.f65482c += i12;
        return i12;
    }

    @Override // java.io.DataInput
    public final void readFully(byte[] bArr) throws IOException {
        this.f65482c += bArr.length;
        this.f65481b.readFully(bArr);
    }

    public b(InputStream inputStream, int i10) {
        ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        this.f65481b = dataInputStream;
        dataInputStream.mark(0);
        this.f65482c = 0;
        this.f65483e = byteOrder;
        this.f65485m = inputStream instanceof b ? ((b) inputStream).f65485m : -1;
    }
}

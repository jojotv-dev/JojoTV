package jcifs.pac;

import androidx.media3.exoplayer.Renderer;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Date;
import jcifs.smb.SID;
import kotlin.UShort;

/* JADX INFO: loaded from: classes5.dex */
public class PacDataInputStream {
    private DataInputStream dis;
    private int size;

    public PacDataInputStream(InputStream inputStream) {
        this.dis = new DataInputStream(inputStream);
        this.size = inputStream.available();
    }

    public void align(int i10) throws IOException {
        int iAvailable = (this.size - this.dis.available()) & (i10 - 1);
        if (i10 == 0 || iAvailable == 0) {
            return;
        }
        this.dis.skip(i10 - iAvailable);
    }

    public int available() {
        return this.dis.available();
    }

    public byte readByte() {
        return this.dis.readByte();
    }

    public char readChar() throws IOException {
        align(2);
        return this.dis.readChar();
    }

    public Date readFiletime() {
        long unsignedInt = readUnsignedInt();
        long unsignedInt2 = readUnsignedInt();
        if (unsignedInt2 == 2147483647L || unsignedInt == 4294967295L) {
            return null;
        }
        return new Date(BigInteger.valueOf(unsignedInt).add(BigInteger.valueOf(unsignedInt2).shiftLeft(32)).divide(BigInteger.valueOf(Renderer.DEFAULT_DURATION_TO_PROGRESS_US)).add(BigInteger.valueOf(-11644473600000L)).longValue());
    }

    public void readFully(byte[] bArr) throws IOException {
        this.dis.readFully(bArr);
    }

    public SID readId() throws IOException {
        byte[] bArr = new byte[4];
        readFully(bArr);
        byte[] bArr2 = new byte[12];
        bArr2[0] = 1;
        bArr2[1] = (byte) 1;
        System.arraycopy(new byte[]{0, 0, 0, 0, 0, 5}, 0, bArr2, 2, 6);
        System.arraycopy(bArr, 0, bArr2, 8, 4);
        return new SID(bArr2, 0);
    }

    public int readInt() throws IOException {
        align(4);
        return Integer.reverseBytes(this.dis.readInt());
    }

    public long readLong() throws IOException {
        align(8);
        return Long.reverseBytes(this.dis.readLong());
    }

    public short readShort() throws IOException {
        align(2);
        return Short.reverseBytes(this.dis.readShort());
    }

    public SID readSid() throws IOException {
        byte[] bArr = new byte[(readInt() * 4) + 8];
        readFully(bArr);
        return new SID(bArr, 0);
    }

    public String readString() throws IOException {
        int i10 = readInt();
        int i11 = readInt();
        int i12 = readInt();
        if (i11 > i10 || i12 > i10 - i11) {
            throw new PACDecodingException("Malformed string in PAC");
        }
        this.dis.skip(i11 * 2);
        char[] cArr = new char[i12];
        for (int i13 = 0; i13 < i12; i13++) {
            cArr[i13] = (char) readShort();
        }
        return new String(cArr);
    }

    public PacUnicodeString readUnicodeString() throws IOException {
        short s2 = readShort();
        short s4 = readShort();
        int i10 = readInt();
        if (s4 >= s2) {
            return new PacUnicodeString(s2, s4, i10);
        }
        throw new PACDecodingException("Malformed string in PAC");
    }

    public int readUnsignedByte() {
        return readByte() & 255;
    }

    public long readUnsignedInt() {
        return ((long) readInt()) & 4294967295L;
    }

    public int readUnsignedShort() {
        return readShort() & UShort.MAX_VALUE;
    }

    public int skipBytes(int i10) {
        return this.dis.skipBytes(i10);
    }

    public void readFully(byte[] bArr, int i10, int i11) throws IOException {
        this.dis.readFully(bArr, i10, i11);
    }
}

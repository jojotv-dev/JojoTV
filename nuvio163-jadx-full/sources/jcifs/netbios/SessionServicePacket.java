package jcifs.netbios;

import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes5.dex */
public abstract class SessionServicePacket {
    static final int HEADER_LENGTH = 4;
    static final int MAX_MESSAGE_SIZE = 131071;
    public static final int NEGATIVE_SESSION_RESPONSE = 131;
    public static final int POSITIVE_SESSION_RESPONSE = 130;
    static final int SESSION_KEEP_ALIVE = 133;
    static final int SESSION_MESSAGE = 0;
    static final int SESSION_REQUEST = 129;
    static final int SESSION_RETARGET_RESPONSE = 132;
    int length;
    int type;

    public static int readInt2(byte[] bArr, int i10) {
        return ((bArr[i10] & 255) << 8) + (bArr[i10 + 1] & 255);
    }

    public static int readInt4(byte[] bArr, int i10) {
        return ((bArr[i10] & 255) << 24) + ((bArr[i10 + 1] & 255) << 16) + ((bArr[i10 + 2] & 255) << 8) + (bArr[i10 + 3] & 255);
    }

    public static int readLength(byte[] bArr, int i10) {
        int i11 = i10 + 2;
        return ((bArr[i10 + 1] & 1) << 16) + ((bArr[i11] & 255) << 8) + (bArr[i10 + 3] & 255);
    }

    public static int readPacketType(InputStream inputStream, byte[] bArr, int i10) throws IOException {
        int nVar = readn(inputStream, bArr, i10, 4);
        if (nVar == 4) {
            return bArr[i10] & 255;
        }
        if (nVar == -1) {
            return -1;
        }
        throw new IOException("unexpected EOF reading netbios session header");
    }

    public static int readn(InputStream inputStream, byte[] bArr, int i10, int i11) throws IOException {
        int i12 = 0;
        while (i12 < i11) {
            int i13 = inputStream.read(bArr, i10 + i12, i11 - i12);
            if (i13 <= 0) {
                break;
            }
            i12 += i13;
        }
        return i12;
    }

    public static void writeInt2(int i10, byte[] bArr, int i11) {
        bArr[i11] = (byte) ((i10 >> 8) & 255);
        bArr[i11 + 1] = (byte) (i10 & 255);
    }

    public static void writeInt4(int i10, byte[] bArr, int i11) {
        bArr[i11] = (byte) ((i10 >> 24) & 255);
        bArr[i11 + 1] = (byte) ((i10 >> 16) & 255);
        bArr[i11 + 2] = (byte) ((i10 >> 8) & 255);
        bArr[i11 + 3] = (byte) (i10 & 255);
    }

    public int readHeaderWireFormat(InputStream inputStream, byte[] bArr, int i10) {
        this.type = bArr[i10] & 255;
        this.length = readInt2(bArr, i10 + 2) + ((bArr[i10 + 1] & 1) << 16);
        return 4;
    }

    public abstract int readTrailerWireFormat(InputStream inputStream, byte[] bArr, int i10);

    public int readWireFormat(InputStream inputStream, byte[] bArr, int i10) {
        readHeaderWireFormat(inputStream, bArr, i10);
        return readTrailerWireFormat(inputStream, bArr, i10) + 4;
    }

    public int writeHeaderWireFormat(byte[] bArr, int i10) {
        int i11 = i10 + 1;
        bArr[i10] = (byte) this.type;
        int i12 = this.length;
        if (i12 > 65535) {
            bArr[i11] = 1;
        }
        writeInt2(i12, bArr, i10 + 2);
        return 4;
    }

    public abstract int writeTrailerWireFormat(byte[] bArr, int i10);

    public int writeWireFormat(byte[] bArr, int i10) {
        this.length = writeTrailerWireFormat(bArr, i10 + 4);
        writeHeaderWireFormat(bArr, i10);
        return this.length + 4;
    }
}

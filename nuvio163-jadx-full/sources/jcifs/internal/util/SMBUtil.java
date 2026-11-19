package jcifs.internal.util;

import androidx.media3.exoplayer.Renderer;
import jcifs.SmbConstants;
import jcifs.internal.smb1.trans.SmbComTransaction;

/* JADX INFO: loaded from: classes5.dex */
public class SMBUtil {
    public static final byte[] SMB_HEADER = {-1, SmbComTransaction.TRANS_WAIT_NAMED_PIPE, 77, 66, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public static final byte[] SMB2_HEADER = {-2, SmbComTransaction.TRANS_WAIT_NAMED_PIPE, 77, 66, 64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    public static int readInt2(byte[] bArr, int i10) {
        return (bArr[i10] & 255) + ((bArr[i10 + 1] & 255) << 8);
    }

    public static int readInt4(byte[] bArr, int i10) {
        return (bArr[i10] & 255) + ((bArr[i10 + 1] & 255) << 8) + ((bArr[i10 + 2] & 255) << 16) + ((bArr[i10 + 3] & 255) << 24);
    }

    public static long readInt8(byte[] bArr, int i10) {
        return (((long) readInt4(bArr, i10)) & 4294967295L) + (((long) readInt4(bArr, i10 + 4)) << 32);
    }

    public static long readTime(byte[] bArr, int i10) {
        return (((((long) readInt4(bArr, i10 + 4)) << 32) | (((long) readInt4(bArr, i10)) & 4294967295L)) / Renderer.DEFAULT_DURATION_TO_PROGRESS_US) - SmbConstants.MILLISECONDS_BETWEEN_1970_AND_1601;
    }

    public static long readUTime(byte[] bArr, int i10) {
        return (((long) readInt4(bArr, i10)) & 4294967295L) * 1000;
    }

    public static void writeInt2(long j10, byte[] bArr, int i10) {
        bArr[i10] = (byte) j10;
        bArr[i10 + 1] = (byte) (j10 >> 8);
    }

    public static void writeInt4(long j10, byte[] bArr, int i10) {
        bArr[i10] = (byte) j10;
        bArr[i10 + 1] = (byte) (j10 >> 8);
        bArr[i10 + 2] = (byte) (j10 >> 16);
        bArr[i10 + 3] = (byte) (j10 >> 24);
    }

    public static void writeInt8(long j10, byte[] bArr, int i10) {
        bArr[i10] = (byte) j10;
        bArr[i10 + 1] = (byte) (j10 >> 8);
        bArr[i10 + 2] = (byte) (j10 >> 16);
        bArr[i10 + 3] = (byte) (j10 >> 24);
        bArr[i10 + 4] = (byte) (j10 >> 32);
        bArr[i10 + 5] = (byte) (j10 >> 40);
        bArr[i10 + 6] = (byte) (j10 >> 48);
        bArr[i10 + 7] = (byte) (j10 >> 56);
    }

    public static void writeTime(long j10, byte[] bArr, int i10) {
        if (j10 != 0) {
            j10 = (j10 + SmbConstants.MILLISECONDS_BETWEEN_1970_AND_1601) * Renderer.DEFAULT_DURATION_TO_PROGRESS_US;
        }
        writeInt8(j10, bArr, i10);
    }

    public static void writeUTime(long j10, byte[] bArr, int i10) {
        writeInt4(j10 / 1000, bArr, i10);
    }
}

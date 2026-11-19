package rn;

import androidx.media3.extractor.ts.TsExtractor;
import io.ktor.util.date.GMTDateParser;
import java.io.IOException;

/* JADX INFO: loaded from: classes5.dex */
public abstract class d {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final char[] f53962a = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', GMTDateParser.DAY_OF_MONTH, 'e', 'f'};

    public static int a(int i10, byte[] bArr) {
        return (bArr[i10 + 3] & 255) | (bArr[i10] << 24) | ((bArr[i10 + 1] & 255) << 16) | ((bArr[i10 + 2] & 255) << 8);
    }

    public static byte[] b(byte[] bArr) {
        if (bArr == null) {
            return null;
        }
        return (byte[]) bArr.clone();
    }

    public static short[] c(short[] sArr) {
        if (sArr == null) {
            return null;
        }
        return (short[]) sArr.clone();
    }

    public static byte[] d(byte[] bArr, byte[] bArr2) {
        if (bArr == null) {
            return b(bArr2);
        }
        if (bArr2 == null) {
            return b(bArr);
        }
        byte[] bArr3 = new byte[bArr.length + bArr2.length];
        System.arraycopy(bArr, 0, bArr3, 0, bArr.length);
        System.arraycopy(bArr2, 0, bArr3, bArr.length, bArr2.length);
        return bArr3;
    }

    public static byte[] e(byte[][] bArr) {
        int length = 0;
        for (int i10 = 0; i10 != bArr.length; i10++) {
            length += bArr[i10].length;
        }
        byte[] bArr2 = new byte[length];
        int length2 = 0;
        for (int i11 = 0; i11 != bArr.length; i11++) {
            byte[] bArr3 = bArr[i11];
            System.arraycopy(bArr3, 0, bArr2, length2, bArr3.length);
            length2 += bArr[i11].length;
        }
        return bArr2;
    }

    public static boolean f(byte[] bArr, int i10, int i11, byte[] bArr2, int i12) {
        if (bArr == null) {
            throw new NullPointerException("'a' cannot be null");
        }
        if (i10 < 0) {
            throw new IllegalArgumentException("'len' cannot be negative");
        }
        if (i11 > bArr.length - i10) {
            throw new IndexOutOfBoundsException("'aOff' value invalid for specified length");
        }
        if (i12 > bArr2.length - i10) {
            throw new IndexOutOfBoundsException("'bOff' value invalid for specified length");
        }
        int i13 = 0;
        for (int i14 = 0; i14 < i10; i14++) {
            i13 |= bArr[i11 + i14] ^ bArr2[i12 + i14];
        }
        return i13 == 0;
    }

    public static boolean g(byte[] bArr, byte[] bArr2) {
        if (bArr != null && bArr2 != null) {
            if (bArr == bArr2) {
                return true;
            }
            int length = bArr.length < bArr2.length ? bArr.length : bArr2.length;
            int length2 = bArr.length ^ bArr2.length;
            for (int i10 = 0; i10 != length; i10++) {
                length2 |= bArr[i10] ^ bArr2[i10];
            }
            while (length < bArr2.length) {
                byte b5 = bArr2[length];
                length2 |= b5 ^ (~b5);
                length++;
            }
            if (length2 == 0) {
                return true;
            }
        }
        return false;
    }

    public static byte[] h(byte[] bArr, int i10, int i11) {
        int iJ = j(i10, i11);
        byte[] bArr2 = new byte[iJ];
        System.arraycopy(bArr, i10, bArr2, 0, Math.min(bArr.length - i10, iJ));
        return bArr2;
    }

    public static String i(Object obj) {
        Class<?> cls = obj == null ? null : obj.getClass();
        return cls == null ? "null" : cls.getName();
    }

    public static int j(int i10, int i11) {
        int i12 = i11 - i10;
        if (i12 >= 0) {
            return i12;
        }
        throw new IllegalArgumentException(i10 + " > " + i11);
    }

    public static int k(int i10, int[] iArr) {
        if (iArr == null) {
            return 0;
        }
        int i11 = i10 + 1;
        while (true) {
            i10--;
            if (i10 < 0) {
                return i11;
            }
            i11 = (i11 * TsExtractor.TS_STREAM_TYPE_AIT) ^ iArr[i10];
        }
    }

    public static int l(byte[] bArr) {
        if (bArr == null) {
            return 0;
        }
        int length = bArr.length;
        int i10 = length + 1;
        while (true) {
            length--;
            if (length < 0) {
                return i10;
            }
            i10 = (i10 * TsExtractor.TS_STREAM_TYPE_AIT) ^ bArr[length];
        }
    }

    public static int m(long[] jArr, int i10) {
        if (jArr == null) {
            return 0;
        }
        int i11 = i10 + 1;
        while (true) {
            i10--;
            if (i10 < 0) {
                return i11;
            }
            long j10 = jArr[i10];
            i11 = (((i11 * TsExtractor.TS_STREAM_TYPE_AIT) ^ ((int) j10)) * TsExtractor.TS_STREAM_TYPE_AIT) ^ ((int) (j10 >>> 32));
        }
    }

    public static IllegalArgumentException n(Exception exc, String str) {
        return (IllegalArgumentException) new IllegalArgumentException(str).initCause(exc);
    }

    public static IllegalStateException o(String str, IOException iOException) {
        return (IllegalStateException) new IllegalStateException(str).initCause(iOException);
    }

    public static void p(int i10, int i11, byte[] bArr) {
        bArr[i11] = (byte) (i10 >>> 24);
        bArr[i11 + 1] = (byte) (i10 >>> 16);
        bArr[i11 + 2] = (byte) (i10 >>> 8);
        bArr[i11 + 3] = (byte) i10;
    }

    public static void q(int i10, int i11, byte[] bArr) {
        bArr[i11] = (byte) i10;
        bArr[i11 + 1] = (byte) (i10 >>> 8);
        bArr[i11 + 2] = (byte) (i10 >>> 16);
        bArr[i11 + 3] = (byte) (i10 >>> 24);
    }

    public static IOException r(Exception exc, String str) {
        return (IOException) new IOException(str).initCause(exc);
    }

    public static int s(int i10, byte[] bArr) {
        return (bArr[i10 + 3] << 24) | (bArr[i10] & 255) | ((bArr[i10 + 1] & 255) << 8) | ((bArr[i10 + 2] & 255) << 16);
    }

    public static void t(byte[] bArr, int[] iArr, int i10) {
        int i11 = 0;
        for (int i12 = 0; i12 < i10; i12++) {
            iArr[i12] = s(i11, bArr);
            i11 += 4;
        }
    }

    public static long u(int i10, byte[] bArr) {
        return ((((long) s(i10 + 4, bArr)) & 4294967295L) << 32) | (((long) s(i10, bArr)) & 4294967295L);
    }

    public static void v(long j10, byte[] bArr, int i10) {
        p((int) (j10 >>> 32), i10, bArr);
        p((int) (j10 & 4294967295L), i10 + 4, bArr);
    }

    public static void w(long j10, byte[] bArr, int i10) {
        q((int) (4294967295L & j10), i10, bArr);
        q((int) (j10 >>> 32), i10 + 4, bArr);
    }

    public static boolean x(int i10, int i11, int i12, int i13) {
        return i11 > 0 && i13 > 0 && i10 - i12 < i13 && i12 - i10 < i11;
    }
}

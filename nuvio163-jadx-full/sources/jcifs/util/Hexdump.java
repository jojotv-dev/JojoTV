package jcifs.util;

/* JADX INFO: loaded from: classes5.dex */
public class Hexdump {
    public static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static void toHexChars(int i10, char[] cArr, int i11, int i12) {
        while (i12 > 0) {
            int i13 = (i11 + i12) - 1;
            if (i13 < cArr.length) {
                cArr[i13] = HEX_DIGITS[i10 & 15];
            }
            if (i10 != 0) {
                i10 >>>= 4;
            }
            i12--;
        }
    }

    public static String toHexString(int i10, int i11) {
        char[] cArr = new char[i11];
        toHexChars(i10, cArr, 0, i11);
        return new String(cArr);
    }

    public static void toHexChars(long j10, char[] cArr, int i10, int i11) {
        while (i11 > 0) {
            cArr[(i10 + i11) - 1] = HEX_DIGITS[(int) (15 & j10)];
            if (j10 != 0) {
                j10 >>>= 4;
            }
            i11--;
        }
    }

    public static String toHexString(long j10, int i10) {
        char[] cArr = new char[i10];
        toHexChars(j10, cArr, 0, i10);
        return new String(cArr);
    }

    public static String toHexString(byte[] bArr, int i10, int i11) {
        char[] cArr = new char[i11 * 2];
        int i12 = 0;
        for (int i13 = 0; i13 < i11; i13++) {
            int i14 = i12 + 1;
            char[] cArr2 = HEX_DIGITS;
            byte b5 = bArr[i10 + i13];
            cArr[i12] = cArr2[(b5 >> 4) & 15];
            i12 += 2;
            cArr[i14] = cArr2[b5 & 15];
        }
        return new String(cArr);
    }

    public static String toHexString(byte[] bArr) {
        return toHexString(bArr, 0, bArr.length);
    }
}

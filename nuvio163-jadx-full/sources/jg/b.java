package jg;

import java.security.MessageDigest;
import java.util.Arrays;
import okio.Utf8;

/* JADX INFO: loaded from: classes4.dex */
public final class b {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final byte[] f27497a;

    public b(byte[] bArr) {
        this.f27497a = Arrays.copyOf(bArr, bArr.length);
    }

    public static int a(int i10, char[] cArr) {
        int i11 = 0;
        for (int i12 = 0; i12 < 4; i12++) {
            char c5 = cArr[i12 + i10];
            i11 |= ((((((((('@' - c5) & (c5 - '[')) >>> 8) & (c5 - '@')) - 1) + (((('`' - c5) & (c5 - '{')) >>> 8) & (c5 - 'F'))) + (((('/' - c5) & (c5 - ':')) >>> 8) & (c5 + 5))) + (((('*' - c5) & (c5 - ',')) >>> 8) & 63)) + ((((c5 - '0') & ('.' - c5)) >>> 8) & 64)) << (18 - (i12 * 6));
        }
        return i11;
    }

    public static void b(byte[] bArr, int i10, int i11, char[] cArr) {
        byte b5 = bArr[i10];
        byte b10 = bArr[i10 + 1];
        byte b11 = bArr[i10 + 2];
        byte[] bArr2 = {(byte) ((b5 >>> 2) & 63), (byte) (((b5 << 4) | ((b10 & 255) >>> 4)) & 63), (byte) ((((b11 & 255) >>> 6) | (b10 << 2)) & 63), (byte) (b11 & Utf8.REPLACEMENT_BYTE)};
        for (int i12 = 0; i12 < 4; i12++) {
            byte b12 = bArr2[i12];
            cArr[i12 + i11] = (char) (((((b12 + 65) + (((25 - b12) >>> 8) & 6)) - (((51 - b12) >>> 8) & 75)) - (((61 - b12) >>> 8) & 15)) + (((62 - b12) >>> 8) & 3));
        }
    }

    public static b c(String str) throws c {
        char[] charArray = str.toCharArray();
        if (charArray.length != 44 || charArray[43] != '=') {
            throw new c();
        }
        byte[] bArr = new byte[32];
        int i10 = 0;
        int i11 = 0;
        while (i10 < 10) {
            int iA = a(i10 * 4, charArray);
            i11 |= iA >>> 31;
            int i12 = i10 * 3;
            bArr[i12] = (byte) ((iA >>> 16) & 255);
            bArr[i12 + 1] = (byte) ((iA >>> 8) & 255);
            bArr[i12 + 2] = (byte) (iA & 255);
            i10++;
        }
        int i13 = i10 * 4;
        int iA2 = a(0, new char[]{charArray[i13], charArray[i13 + 1], charArray[i13 + 2], 'A'});
        int i14 = (iA2 >>> 31) | (iA2 & 255) | i11;
        int i15 = i10 * 3;
        bArr[i15] = (byte) ((iA2 >>> 16) & 255);
        bArr[i15 + 1] = (byte) ((iA2 >>> 8) & 255);
        if (i14 == 0) {
            return new b(bArr);
        }
        throw new c();
    }

    public final String d() {
        char[] cArr = new char[44];
        int i10 = 0;
        while (true) {
            byte[] bArr = this.f27497a;
            if (i10 >= bArr.length / 3) {
                int i11 = i10 * 3;
                b(new byte[]{bArr[i11], bArr[i11 + 1], 0}, 0, i10 * 4, cArr);
                cArr[43] = '=';
                return new String(cArr);
            }
            b(bArr, i10 * 3, i10 * 4, cArr);
            i10++;
        }
    }

    public final String e() {
        char[] cArr = new char[64];
        int i10 = 0;
        while (true) {
            byte[] bArr = this.f27497a;
            if (i10 >= bArr.length) {
                return new String(cArr);
            }
            int i11 = i10 * 2;
            byte b5 = bArr[i10];
            cArr[i11] = (char) (((b5 >> 4) & 15) + 87 + (((((b5 >> 4) & 15) - 10) >> 8) & (-39)));
            cArr[i11 + 1] = (char) ((b5 & 15) + 87 + ((((b5 & 15) - 10) >> 8) & (-39)));
            i10++;
        }
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != b.class) {
            return false;
        }
        return MessageDigest.isEqual(this.f27497a, ((b) obj).f27497a);
    }

    public final int hashCode() {
        int i10 = 0;
        int i11 = 0;
        while (true) {
            byte[] bArr = this.f27497a;
            if (i10 >= bArr.length / 4) {
                return i11;
            }
            int i12 = i10 * 4;
            i11 ^= ((bArr[i12] + (bArr[i12 + 1] >> 8)) + (bArr[i12 + 2] >> 16)) + (bArr[i12 + 3] >> 24);
            i10++;
        }
    }
}

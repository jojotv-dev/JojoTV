package j$.util;

import com.fasterxml.jackson.core.JsonPointer;
import io.ktor.util.date.GMTDateParser;
import java.util.Arrays;

/* JADX INFO: loaded from: classes2.dex */
public class Base64 {
    public static Encoder getEncoder() {
        return Encoder.f26756e;
    }

    public static Encoder getUrlEncoder() {
        return Encoder.f26757f;
    }

    public static Decoder getDecoder() {
        return Decoder.f26753c;
    }

    public static class Encoder {

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        public static final char[] f26754c = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', GMTDateParser.MONTH, 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', GMTDateParser.YEAR, 'Z', 'a', 'b', 'c', GMTDateParser.DAY_OF_MONTH, 'e', 'f', 'g', GMTDateParser.HOURS, 'i', 'j', 'k', 'l', GMTDateParser.MINUTES, 'n', 'o', 'p', 'q', 'r', GMTDateParser.SECONDS, 't', 'u', 'v', 'w', 'x', 'y', GMTDateParser.ZONE, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', JsonPointer.SEPARATOR};

        /* JADX INFO: renamed from: d, reason: collision with root package name */
        public static final char[] f26755d = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', GMTDateParser.MONTH, 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', GMTDateParser.YEAR, 'Z', 'a', 'b', 'c', GMTDateParser.DAY_OF_MONTH, 'e', 'f', 'g', GMTDateParser.HOURS, 'i', 'j', 'k', 'l', GMTDateParser.MINUTES, 'n', 'o', 'p', 'q', 'r', GMTDateParser.SECONDS, 't', 'u', 'v', 'w', 'x', 'y', GMTDateParser.ZONE, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'};

        /* JADX INFO: renamed from: e, reason: collision with root package name */
        public static final Encoder f26756e = new Encoder(false, true);

        /* JADX INFO: renamed from: f, reason: collision with root package name */
        public static final Encoder f26757f = new Encoder(true, true);

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        public final boolean f26758a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        public final boolean f26759b;

        public Encoder(boolean z2, boolean z10) {
            this.f26758a = z2;
            this.f26759b = z10;
        }

        public String encodeToString(byte[] bArr) {
            int i10;
            int length = bArr.length;
            boolean z2 = this.f26759b;
            if (z2) {
                i10 = ((length + 2) / 3) * 4;
            } else {
                int i11 = length % 3;
                i10 = ((length / 3) * 4) + (i11 == 0 ? 0 : i11 + 1);
            }
            byte[] bArrCopyOf = new byte[i10];
            int length2 = bArr.length;
            char[] cArr = f26754c;
            char[] cArr2 = f26755d;
            boolean z10 = this.f26758a;
            char[] cArr3 = z10 ? cArr2 : cArr;
            int i12 = (length2 / 3) * 3;
            int i13 = 0;
            int i14 = 0;
            while (i13 < i12) {
                int iMin = Math.min(i13 + i12, i12);
                char[] cArr4 = z10 ? cArr2 : cArr;
                int i15 = i13;
                int i16 = i14;
                while (i15 < iMin) {
                    int i17 = i15 + 2;
                    int i18 = ((bArr[i15] & 255) << 16) | ((bArr[i15 + 1] & 255) << 8);
                    i15 += 3;
                    int i19 = i18 | (bArr[i17] & 255);
                    bArrCopyOf[i16] = (byte) cArr4[(i19 >>> 18) & 63];
                    bArrCopyOf[i16 + 1] = (byte) cArr4[(i19 >>> 12) & 63];
                    int i20 = i16 + 3;
                    bArrCopyOf[i16 + 2] = (byte) cArr4[(i19 >>> 6) & 63];
                    i16 += 4;
                    bArrCopyOf[i20] = (byte) cArr4[i19 & 63];
                }
                int i21 = ((iMin - i13) / 3) * 4;
                i14 += i21;
                if (i21 == -1 && iMin < length2) {
                    throw null;
                }
                i13 = iMin;
            }
            if (i13 < length2) {
                int i22 = i13 + 1;
                int i23 = bArr[i13] & 255;
                int i24 = i14 + 1;
                bArrCopyOf[i14] = (byte) cArr3[i23 >> 2];
                if (i22 == length2) {
                    int i25 = i14 + 2;
                    bArrCopyOf[i24] = (byte) cArr3[(i23 << 4) & 63];
                    if (z2) {
                        int i26 = i14 + 3;
                        bArrCopyOf[i25] = kotlin.io.encoding.Base64.padSymbol;
                        i14 += 4;
                        bArrCopyOf[i26] = kotlin.io.encoding.Base64.padSymbol;
                    } else {
                        i14 = i25;
                    }
                } else {
                    int i27 = bArr[i22] & 255;
                    bArrCopyOf[i24] = (byte) cArr3[((i23 << 4) & 63) | (i27 >> 4)];
                    int i28 = i14 + 3;
                    bArrCopyOf[i14 + 2] = (byte) cArr3[(i27 << 2) & 63];
                    if (z2) {
                        i14 += 4;
                        bArrCopyOf[i28] = kotlin.io.encoding.Base64.padSymbol;
                    } else {
                        i14 = i28;
                    }
                }
            }
            if (i14 != i10) {
                bArrCopyOf = Arrays.copyOf(bArrCopyOf, i14);
            }
            return new String(bArrCopyOf, 0, 0, bArrCopyOf.length);
        }

        public Encoder withoutPadding() {
            return !this.f26759b ? this : new Encoder(this.f26758a, false);
        }
    }

    public static class Decoder {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        public static final int[] f26751a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        public static final int[] f26752b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        public static final Decoder f26753c;

        static {
            int[] iArr = new int[256];
            f26751a = iArr;
            Arrays.fill(iArr, -1);
            for (int i10 = 0; i10 < 64; i10++) {
                f26751a[Encoder.f26754c[i10]] = i10;
            }
            f26751a[61] = -2;
            int[] iArr2 = new int[256];
            f26752b = iArr2;
            Arrays.fill(iArr2, -1);
            for (int i11 = 0; i11 < 64; i11++) {
                f26752b[Encoder.f26755d[i11]] = i11;
            }
            f26752b[61] = -2;
            f26753c = new Decoder();
        }

        /* JADX WARN: Code restructure failed: missing block: B:45:0x00bf, code lost:
        
            if (r9 != 18) goto L56;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public byte[] decode(java.lang.String r21) {
            /*
                Method dump skipped, instruction units count: 346
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.util.Base64.Decoder.decode(java.lang.String):byte[]");
        }
    }
}

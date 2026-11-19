package rn;

import androidx.media3.extractor.ts.PsExtractor;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.AccessController;
import kotlin.jvm.internal.ByteCompanionObject;
import okio.Utf8;

/* JADX INFO: loaded from: classes5.dex */
public abstract class i {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final String f53964a;

    static {
        try {
            try {
                f53964a = (String) AccessController.doPrivileged(new h());
            } catch (Exception unused) {
                f53964a = String.format("%n", new Object[0]);
            }
        } catch (Exception unused2) {
            f53964a = "\n";
        }
    }

    public static String a(byte[] bArr) {
        int length = bArr.length;
        char[] cArr = new char[length];
        for (int i10 = 0; i10 != length; i10++) {
            cArr[i10] = (char) (bArr[i10] & 255);
        }
        return new String(cArr);
    }

    public static String b(byte[] bArr) {
        int length = bArr.length;
        char[] cArr = new char[length];
        short[] sArr = sn.c.f56894a;
        int i10 = 0;
        int i11 = 0;
        loop0: while (i10 < length) {
            int i12 = i10 + 1;
            byte b5 = bArr[i10];
            if (b5 < 0) {
                short s2 = sn.c.f56894a[b5 & ByteCompanionObject.MAX_VALUE];
                int i13 = s2 >>> 8;
                byte b10 = (byte) s2;
                while (true) {
                    if (b10 >= 0) {
                        if (i12 < length) {
                            int i14 = i12 + 1;
                            byte b11 = bArr[i12];
                            i13 = (i13 << 6) | (b11 & Utf8.REPLACEMENT_BYTE);
                            b10 = sn.c.f56895b[b10 + ((b11 & 255) >>> 4)];
                            i12 = i14;
                        }
                    } else if (b10 != -2) {
                        if (i13 <= 65535) {
                            if (i11 < length) {
                                cArr[i11] = (char) i13;
                                i11++;
                                i10 = i12;
                            }
                        } else if (i11 < length - 1) {
                            int i15 = i11 + 1;
                            cArr[i11] = (char) ((i13 >>> 10) + Utf8.HIGH_SURROGATE_HEADER);
                            i11 += 2;
                            cArr[i15] = (char) ((i13 & 1023) | 56320);
                            i10 = i12;
                        }
                    }
                    i11 = -1;
                    break;
                }
            }
            if (i11 >= length) {
                i11 = -1;
                break;
            }
            cArr[i11] = (char) b5;
            i10 = i12;
            i11++;
        }
        if (i11 >= 0) {
            return new String(cArr, 0, i11);
        }
        throw new IllegalArgumentException("Invalid UTF-8 input");
    }

    public static byte[] c(String str) {
        int length = str.length();
        byte[] bArr = new byte[length];
        for (int i10 = 0; i10 != length; i10++) {
            bArr[i10] = (byte) str.charAt(i10);
        }
        return bArr;
    }

    public static String d(String str) {
        char[] charArray = str.toCharArray();
        boolean z2 = false;
        for (int i10 = 0; i10 != charArray.length; i10++) {
            char c5 = charArray[i10];
            if ('A' <= c5 && 'Z' >= c5) {
                charArray[i10] = (char) (c5 + ' ');
                z2 = true;
            }
        }
        return z2 ? new String(charArray) : str;
    }

    public static void e(char[] cArr, int i10, ByteArrayOutputStream byteArrayOutputStream) throws IOException {
        int i11;
        int i12;
        if (i10 < 1) {
            return;
        }
        byte[] bArr = new byte[64];
        int i13 = 0;
        int i14 = 0;
        while (true) {
            int i15 = i13 + 1;
            char c5 = cArr[i13];
            if (c5 < 128) {
                i11 = i14 + 1;
                bArr[i14] = (byte) c5;
            } else {
                if (c5 < 2048) {
                    int i16 = i14 + 1;
                    bArr[i14] = (byte) ((c5 >> 6) | PsExtractor.AUDIO_STREAM);
                    i12 = i14 + 2;
                    bArr[i16] = (byte) ((c5 & '?') | 128);
                } else if (c5 < 55296 || c5 > 57343) {
                    bArr[i14] = (byte) ((c5 >> '\f') | 224);
                    bArr[i14 + 1] = (byte) (((c5 >> 6) & 63) | 128);
                    i11 = i14 + 3;
                    bArr[i14 + 2] = (byte) ((c5 & '?') | 128);
                } else {
                    if (c5 > 56319) {
                        throw new IllegalStateException("invalid UTF-16 high surrogate");
                    }
                    if (i15 >= i10) {
                        throw new IllegalStateException("invalid UTF-16 codepoint (truncated surrogate pair)");
                    }
                    int i17 = i13 + 2;
                    char c10 = cArr[i15];
                    if (c10 < 56320 || c10 > 57343) {
                        throw new IllegalStateException("invalid UTF-16 low surrogate");
                    }
                    int i18 = ((c10 & 1023) | ((c5 & 1023) << 10)) + 65536;
                    bArr[i14] = (byte) ((i18 >> 18) | PsExtractor.VIDEO_STREAM_MASK);
                    bArr[i14 + 1] = (byte) (((i18 >> 12) & 63) | 128);
                    int i19 = i14 + 3;
                    bArr[i14 + 2] = (byte) (((i18 >> 6) & 63) | 128);
                    i12 = i14 + 4;
                    bArr[i19] = (byte) ((i18 & 63) | 128);
                    i15 = i17;
                }
                i11 = i12;
            }
            if (i11 + 4 > 64) {
                byteArrayOutputStream.write(bArr, 0, i11);
                i14 = 0;
            } else {
                i14 = i11;
            }
            if (i15 >= i10) {
                if (i14 > 0) {
                    byteArrayOutputStream.write(bArr, 0, i14);
                    return;
                }
                return;
            }
            i13 = i15;
        }
    }

    public static String f(String str) {
        char[] charArray = str.toCharArray();
        boolean z2 = false;
        for (int i10 = 0; i10 != charArray.length; i10++) {
            char c5 = charArray[i10];
            if ('a' <= c5 && 'z' >= c5) {
                charArray[i10] = (char) (c5 - ' ');
                z2 = true;
            }
        }
        return z2 ? new String(charArray) : str;
    }
}

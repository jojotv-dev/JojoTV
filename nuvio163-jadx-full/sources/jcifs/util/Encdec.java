package jcifs.util;

import androidx.media3.exoplayer.Renderer;
import androidx.media3.extractor.ts.PsExtractor;
import java.io.IOException;
import java.util.Date;
import jcifs.SmbConstants;
import okio.Utf8;

/* JADX INFO: loaded from: classes5.dex */
public final class Encdec {
    private static final long SEC_BETWEEEN_1904_AND_1970 = 2082844800;
    private static final int TIME_1601_NANOS_64BE = 6;
    private static final int TIME_1601_NANOS_64LE = 5;
    private static final int TIME_1904_SEC_32BE = 3;
    private static final int TIME_1904_SEC_32LE = 4;
    private static final int TIME_1970_MILLIS_64BE = 7;
    private static final int TIME_1970_MILLIS_64LE = 8;
    private static final int TIME_1970_SEC_32BE = 1;
    private static final int TIME_1970_SEC_32LE = 2;

    private Encdec() {
    }

    public static double dec_doublebe(byte[] bArr, int i10) {
        return Double.longBitsToDouble(dec_uint64be(bArr, i10));
    }

    public static double dec_doublele(byte[] bArr, int i10) {
        return Double.longBitsToDouble(dec_uint64le(bArr, i10));
    }

    public static float dec_floatbe(byte[] bArr, int i10) {
        return Float.intBitsToFloat(dec_uint32be(bArr, i10));
    }

    public static float dec_floatle(byte[] bArr, int i10) {
        return Float.intBitsToFloat(dec_uint32le(bArr, i10));
    }

    public static Date dec_time(byte[] bArr, int i10, int i11) {
        switch (i11) {
            case 1:
                return new Date(((long) dec_uint32be(bArr, i10)) * 1000);
            case 2:
                return new Date(((long) dec_uint32le(bArr, i10)) * 1000);
            case 3:
                return new Date(((((long) dec_uint32be(bArr, i10)) & 4294967295L) - SEC_BETWEEEN_1904_AND_1970) * 1000);
            case 4:
                return new Date(((((long) dec_uint32le(bArr, i10)) & 4294967295L) - SEC_BETWEEEN_1904_AND_1970) * 1000);
            case 5:
                return new Date((dec_uint64le(bArr, i10) / Renderer.DEFAULT_DURATION_TO_PROGRESS_US) - SmbConstants.MILLISECONDS_BETWEEN_1970_AND_1601);
            case 6:
                return new Date((dec_uint64be(bArr, i10) / Renderer.DEFAULT_DURATION_TO_PROGRESS_US) - SmbConstants.MILLISECONDS_BETWEEN_1970_AND_1601);
            case 7:
                return new Date(dec_uint64be(bArr, i10));
            case 8:
                return new Date(dec_uint64le(bArr, i10));
            default:
                throw new IllegalArgumentException("Unsupported time encoding");
        }
    }

    public static String dec_ucs2le(byte[] bArr, int i10, int i11, char[] cArr) {
        int i12 = 0;
        while (i10 + 1 < i11) {
            char cDec_uint16le = (char) dec_uint16le(bArr, i10);
            cArr[i12] = cDec_uint16le;
            if (cDec_uint16le == 0) {
                break;
            }
            i12++;
            i10 += 2;
        }
        return new String(cArr, 0, i12);
    }

    public static short dec_uint16be(byte[] bArr, int i10) {
        return (short) ((bArr[i10 + 1] & 255) | ((bArr[i10] & 255) << 8));
    }

    public static short dec_uint16le(byte[] bArr, int i10) {
        return (short) (((bArr[i10 + 1] & 255) << 8) | (bArr[i10] & 255));
    }

    public static int dec_uint32be(byte[] bArr, int i10) {
        return (bArr[i10 + 3] & 255) | ((bArr[i10] & 255) << 24) | ((bArr[i10 + 1] & 255) << 16) | ((bArr[i10 + 2] & 255) << 8);
    }

    public static int dec_uint32le(byte[] bArr, int i10) {
        return ((bArr[i10 + 3] & 255) << 24) | (bArr[i10] & 255) | ((bArr[i10 + 1] & 255) << 8) | ((bArr[i10 + 2] & 255) << 16);
    }

    public static long dec_uint64be(byte[] bArr, int i10) {
        return (((long) dec_uint32be(bArr, i10 + 4)) & 4294967295L) | ((((long) dec_uint32be(bArr, i10)) & 4294967295L) << 32);
    }

    public static long dec_uint64le(byte[] bArr, int i10) {
        return (((long) dec_uint32le(bArr, i10)) & 4294967295L) | ((((long) dec_uint32le(bArr, i10 + 4)) & 4294967295L) << 32);
    }

    public static String dec_utf8(byte[] bArr, int i10, int i11) throws IOException {
        char[] cArr = new char[i11 - i10];
        int i12 = 0;
        while (i10 < i11) {
            int i13 = i10 + 1;
            byte b5 = bArr[i10];
            int i14 = b5 & 255;
            if (i14 == 0) {
                break;
            }
            if (i14 < 128) {
                cArr[i12] = (char) i14;
                i10 = i13;
            } else if ((b5 & 224) == 192) {
                if (i11 - i13 < 2) {
                    break;
                }
                char c5 = (char) ((b5 & 31) << 6);
                cArr[i12] = c5;
                i10 += 2;
                byte b10 = bArr[i13];
                char c10 = (char) (c5 | (b10 & Utf8.REPLACEMENT_BYTE));
                cArr[i12] = c10;
                if ((b10 & 192) != 128 || c10 < 128) {
                    throw new IOException("Invalid UTF-8 sequence");
                }
            } else {
                if ((b5 & 240) != 224) {
                    throw new IOException("Unsupported UTF-8 sequence");
                }
                if (i11 - i13 < 3) {
                    break;
                }
                char c11 = (char) ((b5 & 15) << 12);
                cArr[i12] = c11;
                int i15 = i10 + 2;
                byte b11 = bArr[i13];
                if ((b11 & 192) != 128) {
                    throw new IOException("Invalid UTF-8 sequence");
                }
                char c12 = (char) (((b11 & Utf8.REPLACEMENT_BYTE) << 6) | c11);
                cArr[i12] = c12;
                i10 += 3;
                byte b12 = bArr[i15];
                char c13 = (char) (c12 | (b12 & Utf8.REPLACEMENT_BYTE));
                cArr[i12] = c13;
                if ((b12 & 192) != 128 || c13 < 2048) {
                    throw new IOException("Invalid UTF-8 sequence");
                }
            }
            i12++;
        }
        return new String(cArr, 0, i12);
    }

    public static int enc_doublebe(double d10, byte[] bArr, int i10) {
        return enc_uint64be(Double.doubleToLongBits(d10), bArr, i10);
    }

    public static int enc_doublele(double d10, byte[] bArr, int i10) {
        return enc_uint64le(Double.doubleToLongBits(d10), bArr, i10);
    }

    public static int enc_floatbe(float f10, byte[] bArr, int i10) {
        return enc_uint32be(Float.floatToIntBits(f10), bArr, i10);
    }

    public static int enc_floatle(float f10, byte[] bArr, int i10) {
        return enc_uint32le(Float.floatToIntBits(f10), bArr, i10);
    }

    public static int enc_time(Date date, byte[] bArr, int i10, int i11) {
        switch (i11) {
            case 1:
                return enc_uint32be((int) (date.getTime() / 1000), bArr, i10);
            case 2:
                return enc_uint32le((int) (date.getTime() / 1000), bArr, i10);
            case 3:
                return enc_uint32be((int) ((date.getTime() / 1000) + SEC_BETWEEEN_1904_AND_1970), bArr, i10);
            case 4:
                return enc_uint32le((int) ((date.getTime() / 1000) + SEC_BETWEEEN_1904_AND_1970), bArr, i10);
            case 5:
                return enc_uint64le((date.getTime() + SmbConstants.MILLISECONDS_BETWEEN_1970_AND_1601) * Renderer.DEFAULT_DURATION_TO_PROGRESS_US, bArr, i10);
            case 6:
                return enc_uint64be((date.getTime() + SmbConstants.MILLISECONDS_BETWEEN_1970_AND_1601) * Renderer.DEFAULT_DURATION_TO_PROGRESS_US, bArr, i10);
            case 7:
                return enc_uint64be(date.getTime(), bArr, i10);
            case 8:
                return enc_uint64le(date.getTime(), bArr, i10);
            default:
                throw new IllegalArgumentException("Unsupported time encoding");
        }
    }

    public static int enc_uint16be(short s2, byte[] bArr, int i10) {
        bArr[i10] = (byte) ((s2 >> 8) & 255);
        bArr[i10 + 1] = (byte) (s2 & 255);
        return 2;
    }

    public static int enc_uint16le(short s2, byte[] bArr, int i10) {
        bArr[i10] = (byte) (s2 & 255);
        bArr[i10 + 1] = (byte) ((s2 >> 8) & 255);
        return 2;
    }

    public static int enc_uint32be(int i10, byte[] bArr, int i11) {
        bArr[i11] = (byte) ((i10 >> 24) & 255);
        bArr[i11 + 1] = (byte) ((i10 >> 16) & 255);
        bArr[i11 + 2] = (byte) ((i10 >> 8) & 255);
        bArr[i11 + 3] = (byte) (i10 & 255);
        return 4;
    }

    public static int enc_uint32le(int i10, byte[] bArr, int i11) {
        bArr[i11] = (byte) (i10 & 255);
        bArr[i11 + 1] = (byte) ((i10 >> 8) & 255);
        bArr[i11 + 2] = (byte) ((i10 >> 16) & 255);
        bArr[i11 + 3] = (byte) ((i10 >> 24) & 255);
        return 4;
    }

    public static int enc_uint64be(long j10, byte[] bArr, int i10) {
        enc_uint32be((int) (j10 & 4294967295L), bArr, i10 + 4);
        enc_uint32be((int) ((j10 >> 32) & 4294967295L), bArr, i10);
        return 8;
    }

    public static int enc_uint64le(long j10, byte[] bArr, int i10) {
        enc_uint32le((int) (j10 & 4294967295L), bArr, i10);
        enc_uint32le((int) ((j10 >> 32) & 4294967295L), bArr, i10 + 4);
        return 8;
    }

    public static int enc_utf8(String str, byte[] bArr, int i10, int i11) {
        int length = str.length();
        int i12 = i10;
        for (int i13 = 0; i12 < i11 && i13 < length; i13++) {
            char cCharAt = str.charAt(i13);
            if (cCharAt >= 1 && cCharAt <= 127) {
                bArr[i12] = (byte) cCharAt;
                i12++;
            } else if (cCharAt > 2047) {
                if (i11 - i12 < 3) {
                    break;
                }
                bArr[i12] = (byte) (((cCharAt >> '\f') & 15) | 224);
                int i14 = i12 + 2;
                bArr[i12 + 1] = (byte) (((cCharAt >> 6) & 63) | 128);
                i12 += 3;
                bArr[i14] = (byte) ((cCharAt & '?') | 128);
            } else {
                if (i11 - i12 < 2) {
                    break;
                }
                int i15 = i12 + 1;
                bArr[i12] = (byte) (((cCharAt >> 6) & 31) | PsExtractor.AUDIO_STREAM);
                i12 += 2;
                bArr[i15] = (byte) ((cCharAt & '?') | 128);
            }
        }
        return i12 - i10;
    }
}

package up;

import androidx.media3.extractor.ts.PsExtractor;
import io.ktor.util.date.GMTDateParser;

/* JADX INFO: loaded from: classes5.dex */
public final class y {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f60312a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public byte[] f60313b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f60314c;

    public y(int i10) {
        this.f60312a = 1;
        this.f60313b = new byte[i10];
    }

    public static void e(int i10, long j10) {
        long j11 = 1 << i10;
        if (j10 < 0 || j10 > j11) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(j10);
            stringBuffer.append(" out of range for ");
            stringBuffer.append(i10);
            stringBuffer.append(" bit value");
            throw new IllegalArgumentException(stringBuffer.toString());
        }
    }

    public void a(int i10) {
        byte[] bArr = this.f60313b;
        int length = bArr.length * 2;
        int i11 = this.f60314c;
        int i12 = i10 + i11;
        if (length <= i12) {
            length = i12;
        }
        byte[] bArr2 = new byte[length];
        System.arraycopy(bArr, 0, bArr2, 0, i11);
        this.f60313b = bArr2;
    }

    public void b(int i10, int i11) {
        int i12 = this.f60314c;
        int i13 = i12 + 2;
        if (i13 > this.f60313b.length) {
            a(2);
        }
        byte[] bArr = this.f60313b;
        bArr[i12] = (byte) i10;
        bArr[i12 + 1] = (byte) i11;
        this.f60314c = i13;
    }

    public void c(int i10, int i11) {
        int i12 = this.f60314c;
        int i13 = i12 + 3;
        if (i13 > this.f60313b.length) {
            a(3);
        }
        byte[] bArr = this.f60313b;
        bArr[i12] = (byte) i10;
        bArr[i12 + 1] = (byte) (i11 >>> 8);
        bArr[i12 + 2] = (byte) i11;
        this.f60314c = i13;
    }

    public void d(int i10, int i11, String str) {
        int length = str.length();
        int i12 = i10;
        int i13 = i12;
        while (i12 < length) {
            char cCharAt = str.charAt(i12);
            if (cCharAt < 1 || cCharAt > 127) {
                i13 = cCharAt > 2047 ? i13 + 3 : i13 + 2;
            } else {
                i13++;
            }
            i12++;
        }
        if (i13 > i11) {
            throw new IllegalArgumentException();
        }
        int i14 = this.f60314c;
        int i15 = i14 - i10;
        int i16 = i15 - 2;
        if (i16 >= 0) {
            byte[] bArr = this.f60313b;
            bArr[i16] = (byte) (i13 >>> 8);
            bArr[i15 - 1] = (byte) i13;
        }
        if ((i14 + i13) - i10 > this.f60313b.length) {
            a(i13 - i10);
        }
        int i17 = this.f60314c;
        while (i10 < length) {
            char cCharAt2 = str.charAt(i10);
            if (cCharAt2 < 1 || cCharAt2 > 127) {
                byte[] bArr2 = this.f60313b;
                int i18 = i17 + 1;
                if (cCharAt2 > 2047) {
                    bArr2[i17] = (byte) (((cCharAt2 >> '\f') & 15) | 224);
                    int i19 = i17 + 2;
                    bArr2[i18] = (byte) (((cCharAt2 >> 6) & 63) | 128);
                    i17 += 3;
                    bArr2[i19] = (byte) ((cCharAt2 & '?') | 128);
                } else {
                    bArr2[i17] = (byte) (((cCharAt2 >> 6) & 31) | PsExtractor.AUDIO_STREAM);
                    i17 += 2;
                    bArr2[i18] = (byte) ((cCharAt2 & '?') | 128);
                }
            } else {
                this.f60313b[i17] = (byte) cCharAt2;
                i17++;
            }
            i10++;
        }
        this.f60314c = i17;
    }

    public void f(int i10) {
        byte[] bArr = this.f60313b;
        int length = bArr.length;
        int i11 = this.f60314c;
        if (length - i11 >= i10) {
            return;
        }
        int length2 = bArr.length * 2;
        if (length2 < i11 + i10) {
            length2 = i11 + i10;
        }
        byte[] bArr2 = new byte[length2];
        System.arraycopy(bArr, 0, bArr2, 0, i11);
        this.f60313b = bArr2;
    }

    public void g(int i10) {
        int i11 = this.f60314c;
        int i12 = i11 + 1;
        if (i12 > this.f60313b.length) {
            a(1);
        }
        this.f60313b[i11] = (byte) i10;
        this.f60314c = i12;
    }

    public void h(int i10, int i11, byte[] bArr) {
        if (this.f60314c + i11 > this.f60313b.length) {
            a(i11);
        }
        if (bArr != null) {
            System.arraycopy(bArr, i10, this.f60313b, this.f60314c, i11);
        }
        this.f60314c += i11;
    }

    public void i(int i10) {
        int i11 = this.f60314c;
        int i12 = i11 + 4;
        if (i12 > this.f60313b.length) {
            a(4);
        }
        byte[] bArr = this.f60313b;
        bArr[i11] = (byte) (i10 >>> 24);
        bArr[i11 + 1] = (byte) (i10 >>> 16);
        bArr[i11 + 2] = (byte) (i10 >>> 8);
        bArr[i11 + 3] = (byte) i10;
        this.f60314c = i12;
    }

    public void j(long j10) {
        int i10 = this.f60314c;
        int i11 = i10 + 8;
        if (i11 > this.f60313b.length) {
            a(8);
        }
        byte[] bArr = this.f60313b;
        int i12 = (int) (j10 >>> 32);
        bArr[i10] = (byte) (i12 >>> 24);
        bArr[i10 + 1] = (byte) (i12 >>> 16);
        bArr[i10 + 2] = (byte) (i12 >>> 8);
        bArr[i10 + 3] = (byte) i12;
        int i13 = (int) j10;
        bArr[i10 + 4] = (byte) (i13 >>> 24);
        bArr[i10 + 5] = (byte) (i13 >>> 16);
        bArr[i10 + 6] = (byte) (i13 >>> 8);
        bArr[i10 + 7] = (byte) i13;
        this.f60314c = i11;
    }

    public void k(int i10) {
        int i11 = this.f60314c;
        int i12 = i11 + 2;
        if (i12 > this.f60313b.length) {
            a(2);
        }
        byte[] bArr = this.f60313b;
        bArr[i11] = (byte) (i10 >>> 8);
        bArr[i11 + 1] = (byte) i10;
        this.f60314c = i12;
    }

    public byte[] l() {
        int i10 = this.f60314c;
        byte[] bArr = new byte[i10];
        System.arraycopy(this.f60313b, 0, bArr, 0, i10);
        return bArr;
    }

    public void m(int i10, int i11, byte[] bArr) {
        f(i11);
        System.arraycopy(bArr, i10, this.f60313b, this.f60314c, i11);
        this.f60314c += i11;
    }

    public void n(byte[] bArr) {
        m(0, bArr.length, bArr);
    }

    public void o(byte[] bArr) {
        if (bArr.length > 255) {
            throw new IllegalArgumentException("Invalid counted string");
        }
        f(bArr.length + 1);
        byte[] bArr2 = this.f60313b;
        int i10 = this.f60314c;
        this.f60314c = i10 + 1;
        bArr2[i10] = (byte) (255 & bArr.length);
        m(0, bArr.length, bArr);
    }

    public void p(int i10) {
        e(16, i10);
        f(2);
        byte[] bArr = this.f60313b;
        int i11 = this.f60314c;
        int i12 = i11 + 1;
        this.f60314c = i12;
        bArr[i11] = (byte) ((i10 >>> 8) & 255);
        this.f60314c = i11 + 2;
        bArr[i12] = (byte) (i10 & 255);
    }

    public void q(int i10, int i11) {
        e(16, i10);
        if (i11 > this.f60314c - 2) {
            throw new IllegalArgumentException("cannot write past end of data");
        }
        byte[] bArr = this.f60313b;
        bArr[i11] = (byte) ((i10 >>> 8) & 255);
        bArr[i11 + 1] = (byte) (i10 & 255);
    }

    public void r(long j10) {
        e(32, j10);
        f(4);
        byte[] bArr = this.f60313b;
        int i10 = this.f60314c;
        int i11 = i10 + 1;
        this.f60314c = i11;
        bArr[i10] = (byte) ((j10 >>> 24) & 255);
        int i12 = i10 + 2;
        this.f60314c = i12;
        bArr[i11] = (byte) ((j10 >>> 16) & 255);
        int i13 = i10 + 3;
        this.f60314c = i13;
        bArr[i12] = (byte) ((j10 >>> 8) & 255);
        this.f60314c = i10 + 4;
        bArr[i13] = (byte) (j10 & 255);
    }

    public void s(int i10) {
        e(8, i10);
        f(1);
        byte[] bArr = this.f60313b;
        int i11 = this.f60314c;
        this.f60314c = i11 + 1;
        bArr[i11] = (byte) (i10 & 255);
    }

    public String toString() {
        char c5;
        switch (this.f60312a) {
            case 2:
                byte[] bArr = this.f60313b;
                int i10 = this.f60314c;
                byte b5 = bArr[i10];
                StringBuffer stringBuffer = new StringBuffer(b5 * 2);
                for (int i11 = 0; i11 < b5; i11++) {
                    int i12 = i11 * 2;
                    byte b10 = bArr[i12 + i10 + 1];
                    if (b10 == 0) {
                        c5 = '[';
                    } else if (b10 == 1) {
                        c5 = '.';
                    } else if (b10 == 2) {
                        c5 = GMTDateParser.ANY;
                    } else if (b10 != 3) {
                        c5 = '_';
                    } else {
                        stringBuffer.append((int) bArr[i12 + i10 + 2]);
                        c5 = ';';
                    }
                    stringBuffer.append(c5);
                }
                return stringBuffer.toString();
            default:
                return super.toString();
        }
    }

    public y(byte[] bArr, int i10) {
        this.f60312a = 2;
        this.f60313b = bArr;
        this.f60314c = i10;
    }

    public y(byte b5, int i10) {
        this.f60312a = i10;
        switch (i10) {
            case 1:
                this.f60313b = new byte[64];
                break;
            default:
                this.f60313b = new byte[32];
                this.f60314c = 0;
                break;
        }
    }
}

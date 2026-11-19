package dl;

import androidx.media3.exoplayer.audio.SilenceSkippingAudioProcessor;
import androidx.media3.exoplayer.rtsp.SessionDescription;

/* JADX INFO: loaded from: classes5.dex */
public final class k implements Cloneable {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final short[] f13393c = {0, 1, 4, 5, 16, 17, 20, 21, 64, 65, 68, 69, 80, 81, 84, 85, 256, 257, 260, 261, 272, 273, 276, 277, 320, 321, 324, 325, 336, 337, 340, 341, SilenceSkippingAudioProcessor.DEFAULT_SILENCE_THRESHOLD_LEVEL, 1025, 1028, 1029, 1040, 1041, 1044, 1045, 1088, 1089, 1092, 1093, 1104, 1105, 1108, 1109, 1280, 1281, 1284, 1285, 1296, 1297, 1300, 1301, 1344, 1345, 1348, 1349, 1360, 1361, 1364, 1365, 4096, 4097, 4100, 4101, 4112, 4113, 4116, 4117, 4160, 4161, 4164, 4165, 4176, 4177, 4180, 4181, 4352, 4353, 4356, 4357, 4368, 4369, 4372, 4373, 4416, 4417, 4420, 4421, 4432, 4433, 4436, 4437, 5120, 5121, 5124, 5125, 5136, 5137, 5140, 5141, 5184, 5185, 5188, 5189, 5200, 5201, 5204, 5205, 5376, 5377, 5380, 5381, 5392, 5393, 5396, 5397, 5440, 5441, 5444, 5445, 5456, 5457, 5460, 5461, 16384, 16385, 16388, 16389, 16400, 16401, 16404, 16405, 16448, 16449, 16452, 16453, 16464, 16465, 16468, 16469, 16640, 16641, 16644, 16645, 16656, 16657, 16660, 16661, 16704, 16705, 16708, 16709, 16720, 16721, 16724, 16725, 17408, 17409, 17412, 17413, 17424, 17425, 17428, 17429, 17472, 17473, 17476, 17477, 17488, 17489, 17492, 17493, 17664, 17665, 17668, 17669, 17680, 17681, 17684, 17685, 17728, 17729, 17732, 17733, 17744, 17745, 17748, 17749, 20480, 20481, 20484, 20485, 20496, 20497, 20500, 20501, 20544, 20545, 20548, 20549, 20560, 20561, 20564, 20565, 20736, 20737, 20740, 20741, 20752, 20753, 20756, 20757, 20800, 20801, 20804, 20805, 20816, 20817, 20820, 20821, 21504, 21505, 21508, 21509, 21520, 21521, 21524, 21525, 21568, 21569, 21572, 21573, 21584, 21585, 21588, 21589, 21760, 21761, 21764, 21765, 21776, 21777, 21780, 21781, 21824, 21825, 21828, 21829, 21840, 21841, 21844, 21845};

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public long[] f13394b;

    public k(int i10) {
        this.f13394b = new long[i10];
    }

    public static void a(long[] jArr, long[] jArr2, int i10, int i11, int i12) {
        for (int i13 = 0; i13 < i12; i13++) {
            int i14 = i10 + i13;
            jArr[i14] = jArr[i14] ^ jArr2[i11 + i13];
        }
    }

    public static long c(long[] jArr, int i10, long[] jArr2, int i11, int i12, int i13) {
        int i14 = 64 - i13;
        long j10 = 0;
        for (int i15 = 0; i15 < i12; i15++) {
            long j11 = jArr2[i11 + i15];
            int i16 = i10 + i15;
            jArr[i16] = (j10 | (j11 << i13)) ^ jArr[i16];
            j10 = j11 >>> i14;
        }
        return j10;
    }

    public static void f(long[] jArr, int i10) {
        int i11 = i10 >>> 6;
        jArr[i11] = (1 << (i10 & 63)) ^ jArr[i11];
    }

    public static void g(long[] jArr, long[] jArr2, int i10, int i11, int i12) {
        int i13 = i12 >>> 6;
        int i14 = i12 & 63;
        int i15 = i11;
        if (i14 == 0) {
            a(jArr, jArr2, i13, i10, i15);
            return;
        }
        int i16 = i13 + 1;
        int i17 = 64 - i14;
        int i18 = 64 - i17;
        long j10 = 0;
        while (true) {
            i15--;
            if (i15 < 0) {
                jArr[i13] = jArr[i13] ^ j10;
                return;
            }
            long j11 = jArr2[i10 + i15];
            int i19 = i16 + i15;
            jArr[i19] = (j10 | (j11 >>> i17)) ^ jArr[i19];
            j10 = j11 << i18;
        }
    }

    public static void h(long[] jArr, int i10, long j10) {
        int i11 = i10 >>> 6;
        int i12 = i10 & 63;
        if (i12 == 0) {
            jArr[i11] = jArr[i11] ^ j10;
            return;
        }
        jArr[i11] = jArr[i11] ^ (j10 << i12);
        long j11 = j10 >>> (64 - i12);
        if (j11 != 0) {
            int i13 = i11 + 1;
            jArr[i13] = j11 ^ jArr[i13];
        }
    }

    public static long j(int i10) {
        short[] sArr = f13393c;
        int i11 = sArr[i10 & 255] | (sArr[(i10 >>> 8) & 255] << 16);
        return ((((long) ((sArr[i10 >>> 24] << 16) | sArr[(i10 >>> 16) & 255])) & 4294967295L) << 32) | (4294967295L & ((long) i11));
    }

    public static int k(long[] jArr, int i10, int i11, int[] iArr) {
        int i12 = (i11 + 63) >>> 6;
        if (i10 < i12) {
            return i10;
        }
        int i13 = i10 << 6;
        int iMin = Math.min(i13, (i11 << 1) - 1);
        int i14 = i13 - iMin;
        while (i14 >= 64) {
            i10--;
            i14 -= 64;
        }
        int length = iArr.length;
        int i15 = iArr[length - 1];
        int i16 = length > 1 ? iArr[length - 2] : 0;
        int iMax = Math.max(i11, i15 + 64);
        int iMin2 = (Math.min(iMin - iMax, i11 - i16) + i14) >> 6;
        if (iMin2 > 1) {
            int i17 = i10 - iMin2;
            iMin = i17 << 6;
            int i18 = iMin - i11;
            int length2 = iArr.length;
            while (true) {
                length2--;
                if (length2 < 0) {
                    break;
                }
                g(jArr, jArr, i17, i10 - i17, iArr[length2] + i18);
            }
            g(jArr, jArr, i17, i10 - i17, i18);
            while (i10 > i17) {
                i10--;
                jArr[i10] = 0;
            }
        }
        if (iMin > iMax) {
            int i19 = iMax >>> 6;
            while (true) {
                i10--;
                if (i10 <= i19) {
                    break;
                }
                long j10 = jArr[i10];
                if (j10 != 0) {
                    jArr[i10] = 0;
                    int i20 = (i10 << 6) - i11;
                    int length3 = iArr.length;
                    while (true) {
                        length3--;
                        if (length3 < 0) {
                            break;
                        }
                        h(jArr, iArr[length3] + i20, j10);
                    }
                    h(jArr, i20, j10);
                }
            }
            int i21 = iMax & 63;
            long j11 = jArr[i19];
            long j12 = j11 >>> i21;
            if (j12 != 0) {
                jArr[i19] = j11 ^ (j12 << i21);
                int i22 = iMax - i11;
                int length4 = iArr.length;
                while (true) {
                    length4--;
                    if (length4 < 0) {
                        break;
                    }
                    h(jArr, iArr[length4] + i22, j12);
                }
                h(jArr, i22, j12);
            }
        } else {
            iMax = iMin;
        }
        if (iMax > i11) {
            while (true) {
                iMax--;
                if (iMax < i11) {
                    break;
                }
                if (((1 << (iMax & 63)) & jArr[iMax >>> 6]) != 0) {
                    f(jArr, iMax);
                    int i23 = iMax - i11;
                    int length5 = iArr.length;
                    while (true) {
                        length5--;
                        if (length5 < 0) {
                            break;
                        }
                        f(jArr, iArr[length5] + i23);
                    }
                    f(jArr, i23);
                }
            }
        }
        return i12;
    }

    public static void l(long[] jArr, int i10, long[] jArr2, int i11, int i12, int i13) {
        int i14 = 64 - i13;
        long j10 = 0;
        for (int i15 = 0; i15 < i12; i15++) {
            long j11 = jArr[i10 + i15];
            jArr2[i11 + i15] = j10 | (j11 << i13);
            j10 = j11 >>> i14;
        }
    }

    public final void b(k kVar, int i10, int i11) {
        int i12 = (i10 + 63) >>> 6;
        int i13 = i11 >>> 6;
        int i14 = i11 & 63;
        if (i14 == 0) {
            a(this.f13394b, kVar.f13394b, i13, 0, i12);
            return;
        }
        long jC = c(this.f13394b, i13, kVar.f13394b, 0, i12, i14);
        if (jC != 0) {
            long[] jArr = this.f13394b;
            int i15 = i12 + i13;
            jArr[i15] = jC ^ jArr[i15];
        }
    }

    public final Object clone() {
        long[] jArr = this.f13394b;
        long[] jArr2 = jArr == null ? null : (long[]) jArr.clone();
        k kVar = new k();
        kVar.f13394b = jArr2;
        return kVar;
    }

    public final int d() {
        int length = this.f13394b.length;
        while (length != 0) {
            length--;
            long j10 = this.f13394b[length];
            if (j10 != 0) {
                return (64 - Long.numberOfLeadingZeros(j10)) + (length * 64);
            }
        }
        return 0;
    }

    public final int e(int i10) {
        int i11 = (i10 + 62) >>> 6;
        while (i11 != 0) {
            i11--;
            long j10 = this.f13394b[i11];
            if (j10 != 0) {
                return (64 - Long.numberOfLeadingZeros(j10)) + (i11 * 64);
            }
        }
        return 0;
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof k)) {
            return false;
        }
        k kVar = (k) obj;
        int i10 = i();
        if (kVar.i() != i10) {
            return false;
        }
        for (int i11 = 0; i11 < i10; i11++) {
            if (this.f13394b[i11] != kVar.f13394b[i11]) {
                return false;
            }
        }
        return true;
    }

    public final int hashCode() {
        int i10 = i();
        int i11 = 1;
        for (int i12 = 0; i12 < i10; i12++) {
            long j10 = this.f13394b[i12];
            i11 = (((i11 * 31) ^ ((int) j10)) * 31) ^ ((int) (j10 >>> 32));
        }
        return i11;
    }

    public final int i() {
        long[] jArr = this.f13394b;
        int iMin = Math.min(jArr.length, jArr.length);
        if (iMin >= 1) {
            if (jArr[0] != 0) {
                while (true) {
                    int i10 = iMin - 1;
                    if (jArr[i10] != 0) {
                        return iMin;
                    }
                    iMin = i10;
                }
            } else {
                while (true) {
                    int i11 = iMin - 1;
                    if (jArr[i11] != 0) {
                        return iMin;
                    }
                    if (i11 > 0) {
                        iMin = i11;
                    }
                }
            }
        }
        return 0;
    }

    public final String toString() {
        int i10 = i();
        if (i10 == 0) {
            return SessionDescription.SUPPORTED_SDP_VERSION;
        }
        int i11 = i10 - 1;
        StringBuffer stringBuffer = new StringBuffer(Long.toBinaryString(this.f13394b[i11]));
        while (true) {
            i11--;
            if (i11 < 0) {
                return stringBuffer.toString();
            }
            String binaryString = Long.toBinaryString(this.f13394b[i11]);
            int length = binaryString.length();
            if (length < 64) {
                stringBuffer.append("0000000000000000000000000000000000000000000000000000000000000000".substring(length));
            }
            stringBuffer.append(binaryString);
        }
    }

    public k(long[] jArr, int i10) {
        if (i10 == jArr.length) {
            this.f13394b = jArr;
            return;
        }
        long[] jArr2 = new long[i10];
        this.f13394b = jArr2;
        System.arraycopy(jArr, 0, jArr2, 0, i10);
    }
}

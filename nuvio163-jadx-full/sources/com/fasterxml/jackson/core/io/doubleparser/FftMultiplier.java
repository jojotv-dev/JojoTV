package com.fasterxml.jackson.core.io.doubleparser;

import java.math.BigInteger;

/* JADX INFO: loaded from: classes3.dex */
class FftMultiplier {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final int FFT_THRESHOLD = 33220;
    private static final int MAX_MAG_LENGTH = 67108864;
    private static final int ROOTS3_CACHE_SIZE = 20;
    private static final int ROOTS_CACHE2_SIZE = 20;
    private static final int TOOM_COOK_THRESHOLD = 1920;
    public static final double COS_0_25 = Math.cos(0.7853981633974483d);
    public static final double SIN_0_25 = Math.sin(0.7853981633974483d);
    private static volatile ComplexVector[] ROOTS2_CACHE = new ComplexVector[20];
    private static volatile ComplexVector[] ROOTS3_CACHE = new ComplexVector[20];

    public static final class ComplexVector {
        private static final int COMPLEX_SIZE_SHIFT = 1;
        static final int IMAG = 1;
        static final int REAL = 0;

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private final double[] f7490a;
        private final int length;
        private final int offset;

        public ComplexVector(int i10) {
            this.f7490a = new double[i10 << 1];
            this.length = i10;
            this.offset = 0;
        }

        private int imagIdx(int i10) {
            return (i10 << 1) + this.offset + 1;
        }

        private int realIdx(int i10) {
            return (i10 << 1) + this.offset;
        }

        public void add(int i10, MutableComplex mutableComplex) {
            double[] dArr = this.f7490a;
            int iRealIdx = realIdx(i10);
            dArr[iRealIdx] = dArr[iRealIdx] + mutableComplex.real;
            double[] dArr2 = this.f7490a;
            int iImagIdx = imagIdx(i10);
            dArr2[iImagIdx] = dArr2[iImagIdx] + mutableComplex.imag;
        }

        public void addInto(int i10, ComplexVector complexVector, int i11, MutableComplex mutableComplex) {
            mutableComplex.real = this.f7490a[realIdx(i10)] + complexVector.real(i11);
            mutableComplex.imag = this.f7490a[imagIdx(i10)] + complexVector.imag(i11);
        }

        public void addTimesIInto(int i10, ComplexVector complexVector, int i11, MutableComplex mutableComplex) {
            mutableComplex.real = this.f7490a[realIdx(i10)] - complexVector.imag(i11);
            mutableComplex.imag = this.f7490a[imagIdx(i10)] + complexVector.real(i11);
        }

        public void applyInverseWeights(ComplexVector complexVector) {
            int i10 = complexVector.offset;
            double[] dArr = complexVector.f7490a;
            int i11 = this.offset;
            int i12 = (this.length + i11) << 1;
            while (i11 < i12) {
                double[] dArr2 = this.f7490a;
                double d10 = dArr2[i11];
                int i13 = i11 + 1;
                double d11 = dArr2[i13];
                int i14 = i10 + 1;
                dArr2[i11] = FastDoubleSwar.fma(d10, dArr[i10], dArr[i14] * d11);
                this.f7490a[i13] = FastDoubleSwar.fma(-d10, dArr[i14], d11 * dArr[i10]);
                i10 += 2;
                i11 += 2;
            }
        }

        public void applyWeights(ComplexVector complexVector) {
            int i10 = complexVector.offset;
            double[] dArr = complexVector.f7490a;
            int i11 = this.offset;
            int i12 = (this.length + i11) << 1;
            while (i11 < i12) {
                double[] dArr2 = this.f7490a;
                double d10 = dArr2[i11];
                dArr2[i11] = dArr[i10] * d10;
                dArr2[i11 + 1] = d10 * dArr[i10 + 1];
                i10 += 2;
                i11 += 2;
            }
        }

        public void copyInto(int i10, MutableComplex mutableComplex) {
            mutableComplex.real = this.f7490a[realIdx(i10)];
            mutableComplex.imag = this.f7490a[imagIdx(i10)];
        }

        public double imag(int i10) {
            return this.f7490a[(i10 << 1) + this.offset + 1];
        }

        public void multiply(int i10, MutableComplex mutableComplex) {
            int iRealIdx = realIdx(i10);
            int iImagIdx = imagIdx(i10);
            double[] dArr = this.f7490a;
            double d10 = dArr[iRealIdx];
            double d11 = dArr[iImagIdx];
            dArr[iRealIdx] = FastDoubleSwar.fma(d10, mutableComplex.real, (-d11) * mutableComplex.imag);
            this.f7490a[iImagIdx] = FastDoubleSwar.fma(d10, mutableComplex.imag, mutableComplex.real * d11);
        }

        public void multiplyByIAnd(int i10, MutableComplex mutableComplex) {
            int iRealIdx = realIdx(i10);
            int iImagIdx = imagIdx(i10);
            double[] dArr = this.f7490a;
            double d10 = dArr[iRealIdx];
            double d11 = -dArr[iImagIdx];
            dArr[iRealIdx] = FastDoubleSwar.fma(-d10, mutableComplex.imag, mutableComplex.real * d11);
            this.f7490a[iImagIdx] = FastDoubleSwar.fma(d10, mutableComplex.real, d11 * mutableComplex.imag);
        }

        public void multiplyConjugate(int i10, MutableComplex mutableComplex) {
            int iRealIdx = realIdx(i10);
            int iImagIdx = imagIdx(i10);
            double[] dArr = this.f7490a;
            double d10 = dArr[iRealIdx];
            double d11 = dArr[iImagIdx];
            dArr[iRealIdx] = FastDoubleSwar.fma(d10, mutableComplex.real, mutableComplex.imag * d11);
            this.f7490a[iImagIdx] = FastDoubleSwar.fma(-d10, mutableComplex.imag, d11 * mutableComplex.real);
        }

        public void multiplyConjugateInto(int i10, MutableComplex mutableComplex, MutableComplex mutableComplex2) {
            double d10 = this.f7490a[realIdx(i10)];
            double d11 = this.f7490a[imagIdx(i10)];
            mutableComplex2.real = FastDoubleSwar.fma(d10, mutableComplex.real, d11 * mutableComplex.imag);
            mutableComplex2.imag = FastDoubleSwar.fma(-d10, mutableComplex.imag, d11 * mutableComplex.real);
        }

        public void multiplyConjugateTimesI(int i10, MutableComplex mutableComplex) {
            int iRealIdx = realIdx(i10);
            int iImagIdx = imagIdx(i10);
            double[] dArr = this.f7490a;
            double d10 = dArr[iRealIdx];
            double d11 = dArr[iImagIdx];
            double d12 = -d10;
            dArr[iRealIdx] = FastDoubleSwar.fma(d12, mutableComplex.imag, d11 * mutableComplex.real);
            this.f7490a[iImagIdx] = FastDoubleSwar.fma(d12, mutableComplex.real, (-d11) * mutableComplex.imag);
        }

        public void multiplyInto(int i10, MutableComplex mutableComplex, MutableComplex mutableComplex2) {
            double d10 = this.f7490a[realIdx(i10)];
            double d11 = this.f7490a[imagIdx(i10)];
            mutableComplex2.real = FastDoubleSwar.fma(d10, mutableComplex.real, mutableComplex.imag * (-d11));
            mutableComplex2.imag = FastDoubleSwar.fma(d10, mutableComplex.imag, d11 * mutableComplex.real);
        }

        public void multiplyPointwise(ComplexVector complexVector) {
            int i10 = complexVector.offset;
            double[] dArr = complexVector.f7490a;
            int i11 = this.offset;
            int i12 = (this.length + i11) << 1;
            while (i11 < i12) {
                double[] dArr2 = this.f7490a;
                double d10 = dArr2[i11];
                int i13 = i11 + 1;
                double d11 = dArr2[i13];
                double d12 = dArr[i10];
                double d13 = dArr[i10 + 1];
                dArr2[i11] = FastDoubleSwar.fma(d10, d12, (-d11) * d13);
                this.f7490a[i13] = FastDoubleSwar.fma(d10, d13, d11 * d12);
                i10 += 2;
                i11 += 2;
            }
        }

        public double part(int i10, int i11) {
            return this.f7490a[(i10 << 1) + i11];
        }

        public double real(int i10) {
            return this.f7490a[(i10 << 1) + this.offset];
        }

        public void set(int i10, double d10, double d11) {
            int iRealIdx = realIdx(i10);
            double[] dArr = this.f7490a;
            dArr[iRealIdx] = d10;
            dArr[iRealIdx + 1] = d11;
        }

        public void squarePointwise() {
            int i10 = this.offset;
            int i11 = (this.length + i10) << 1;
            while (i10 < i11) {
                double[] dArr = this.f7490a;
                double d10 = dArr[i10];
                int i12 = i10 + 1;
                double d11 = dArr[i12];
                dArr[i10] = FastDoubleSwar.fma(d10, d10, (-d11) * d11);
                this.f7490a[i12] = d10 * 2.0d * d11;
                i10 += 2;
            }
        }

        public void subtractInto(int i10, ComplexVector complexVector, int i11, MutableComplex mutableComplex) {
            mutableComplex.real = this.f7490a[realIdx(i10)] - complexVector.real(i11);
            mutableComplex.imag = this.f7490a[imagIdx(i10)] - complexVector.imag(i11);
        }

        public void subtractTimesIInto(int i10, ComplexVector complexVector, int i11, MutableComplex mutableComplex) {
            mutableComplex.real = this.f7490a[realIdx(i10)] + complexVector.imag(i11);
            mutableComplex.imag = this.f7490a[imagIdx(i10)] - complexVector.real(i11);
        }

        public void timesTwoToThe(int i10, int i11) {
            int iRealIdx = realIdx(i10);
            int iImagIdx = imagIdx(i10);
            double[] dArr = this.f7490a;
            double d10 = dArr[iRealIdx];
            double d11 = dArr[iImagIdx];
            long j10 = i11;
            dArr[iRealIdx] = FastDoubleMath.fastScalb(d10, j10);
            this.f7490a[iImagIdx] = FastDoubleMath.fastScalb(d11, j10);
        }

        public void imag(int i10, double d10) {
            this.f7490a[(i10 << 1) + this.offset + 1] = d10;
        }

        public void real(int i10, double d10) {
            this.f7490a[(i10 << 1) + this.offset] = d10;
        }

        public ComplexVector(ComplexVector complexVector, int i10, int i11) {
            this.length = i11 - i10;
            this.f7490a = complexVector.f7490a;
            this.offset = i10 << 1;
        }
    }

    public static int bitsPerFftPoint(int i10) {
        if (i10 <= 9728) {
            return 19;
        }
        if (i10 <= 18432) {
            return 18;
        }
        if (i10 <= 69632) {
            return 17;
        }
        if (i10 <= 262144) {
            return 16;
        }
        if (i10 <= 983040) {
            return 15;
        }
        if (i10 <= 3670016) {
            return 14;
        }
        if (i10 <= 13631488) {
            return 13;
        }
        if (i10 <= 25165824) {
            return 12;
        }
        if (i10 <= 92274688) {
            return 11;
        }
        if (i10 <= 335544320) {
            return 10;
        }
        return i10 <= 1207959552 ? 9 : 8;
    }

    private static ComplexVector calculateRootsOfUnity(int i10) {
        if (i10 == 1) {
            ComplexVector complexVector = new ComplexVector(1);
            complexVector.real(0, 1.0d);
            complexVector.imag(0, 0.0d);
            return complexVector;
        }
        ComplexVector complexVector2 = new ComplexVector(i10);
        complexVector2.set(0, 1.0d, 0.0d);
        int i11 = i10 / 2;
        complexVector2.set(i11, COS_0_25, SIN_0_25);
        double d10 = 1.5707963267948966d / ((double) i10);
        int i12 = 1;
        while (i12 < i11) {
            double d11 = ((double) i12) * d10;
            double dCos = Math.cos(d11);
            double dSin = Math.sin(d11);
            complexVector2.set(i12, dCos, dSin);
            int i13 = i12;
            complexVector2.set(i10 - i13, dSin, dCos);
            i12 = i13 + 1;
        }
        return complexVector2;
    }

    private static void fft(ComplexVector complexVector, ComplexVector[] complexVectorArr) {
        int i10 = complexVector.length;
        int iNumberOfLeadingZeros = 31 - Integer.numberOfLeadingZeros(i10);
        MutableComplex mutableComplex = new MutableComplex();
        MutableComplex mutableComplex2 = new MutableComplex();
        MutableComplex mutableComplex3 = new MutableComplex();
        MutableComplex mutableComplex4 = new MutableComplex();
        MutableComplex mutableComplex5 = new MutableComplex();
        MutableComplex mutableComplex6 = new MutableComplex();
        while (iNumberOfLeadingZeros >= 2) {
            ComplexVector complexVector2 = complexVectorArr[iNumberOfLeadingZeros - 2];
            int i11 = 1 << iNumberOfLeadingZeros;
            for (int i12 = 0; i12 < i10; i12 += i11) {
                int i13 = 0;
                while (true) {
                    int i14 = i11 / 4;
                    if (i13 < i14) {
                        mutableComplex5.set(complexVector2, i13);
                        mutableComplex5.squareInto(mutableComplex6);
                        int i15 = i12 + i13;
                        int i16 = i14 + i15;
                        int i17 = (i11 / 2) + i15;
                        int i18 = iNumberOfLeadingZeros;
                        int i19 = ((i11 * 3) / 4) + i15;
                        complexVector.addInto(i15, complexVector, i16, mutableComplex);
                        mutableComplex.add(complexVector, i17);
                        mutableComplex.add(complexVector, i19);
                        complexVector.subtractTimesIInto(i15, complexVector, i16, mutableComplex2);
                        mutableComplex2.subtract(complexVector, i17);
                        mutableComplex2.addTimesI(complexVector, i19);
                        mutableComplex2.multiplyConjugate(mutableComplex5);
                        complexVector.subtractInto(i15, complexVector, i16, mutableComplex3);
                        mutableComplex3.add(complexVector, i17);
                        mutableComplex3.subtract(complexVector, i19);
                        mutableComplex3.multiplyConjugate(mutableComplex6);
                        complexVector.addTimesIInto(i15, complexVector, i16, mutableComplex4);
                        mutableComplex4.subtract(complexVector, i17);
                        mutableComplex4.subtractTimesI(complexVector, i19);
                        mutableComplex4.multiply(mutableComplex5);
                        mutableComplex.copyInto(complexVector, i15);
                        mutableComplex2.copyInto(complexVector, i16);
                        mutableComplex3.copyInto(complexVector, i17);
                        mutableComplex4.copyInto(complexVector, i19);
                        i13++;
                        iNumberOfLeadingZeros = i18;
                    }
                }
            }
            iNumberOfLeadingZeros -= 2;
        }
        if (iNumberOfLeadingZeros > 0) {
            for (int i20 = 0; i20 < i10; i20 += 2) {
                complexVector.copyInto(i20, mutableComplex);
                int i21 = i20 + 1;
                complexVector.copyInto(i21, mutableComplex2);
                complexVector.add(i20, mutableComplex2);
                mutableComplex.subtractInto(mutableComplex2, complexVector, i21);
            }
        }
    }

    private static void fft3(ComplexVector complexVector, ComplexVector complexVector2, ComplexVector complexVector3, int i10, double d10) {
        double dSqrt = Math.sqrt(3.0d) * ((double) i10) * (-0.5d);
        for (int i11 = 0; i11 < complexVector.length; i11++) {
            double dReal = complexVector.real(i11) + complexVector2.real(i11) + complexVector3.real(i11);
            double dImag = complexVector.imag(i11) + complexVector2.imag(i11) + complexVector3.imag(i11);
            double dImag2 = (complexVector3.imag(i11) - complexVector2.imag(i11)) * dSqrt;
            double dReal2 = (complexVector2.real(i11) - complexVector3.real(i11)) * dSqrt;
            double dReal3 = (complexVector2.real(i11) + complexVector3.real(i11)) * 0.5d;
            double dImag3 = (complexVector2.imag(i11) + complexVector3.imag(i11)) * 0.5d;
            double dReal4 = (complexVector.real(i11) - dReal3) + dImag2;
            double dImag4 = (complexVector.imag(i11) + dReal2) - dImag3;
            double dReal5 = (complexVector.real(i11) - dReal3) - dImag2;
            double dImag5 = (complexVector.imag(i11) - dReal2) - dImag3;
            complexVector.real(i11, dReal * d10);
            complexVector.imag(i11, dImag * d10);
            complexVector2.real(i11, dReal4 * d10);
            complexVector2.imag(i11, dImag4 * d10);
            complexVector3.real(i11, dReal5 * d10);
            complexVector3.imag(i11, dImag5 * d10);
        }
    }

    private static void fftMixedRadix(ComplexVector complexVector, ComplexVector[] complexVectorArr, ComplexVector complexVector2) {
        int i10 = complexVector.length / 3;
        ComplexVector complexVector3 = new ComplexVector(complexVector, 0, i10);
        int i11 = i10 * 2;
        ComplexVector complexVector4 = new ComplexVector(complexVector, i10, i11);
        ComplexVector complexVector5 = new ComplexVector(complexVector, i11, complexVector.length);
        fft3(complexVector3, complexVector4, complexVector5, 1, 1.0d);
        MutableComplex mutableComplex = new MutableComplex();
        for (int i12 = 0; i12 < complexVector.length / 4; i12++) {
            mutableComplex.set(complexVector2, i12);
            complexVector4.multiplyConjugate(i12, mutableComplex);
            complexVector5.multiplyConjugate(i12, mutableComplex);
            complexVector5.multiplyConjugate(i12, mutableComplex);
        }
        for (int i13 = complexVector.length / 4; i13 < i10; i13++) {
            mutableComplex.set(complexVector2, i13 - (complexVector.length / 4));
            complexVector4.multiplyConjugateTimesI(i13, mutableComplex);
            complexVector5.multiplyConjugateTimesI(i13, mutableComplex);
            complexVector5.multiplyConjugateTimesI(i13, mutableComplex);
        }
        fft(complexVector3, complexVectorArr);
        fft(complexVector4, complexVectorArr);
        fft(complexVector5, complexVectorArr);
    }

    public static BigInteger fromFftVector(ComplexVector complexVector, int i10, int i11) {
        int i12 = i11;
        long j10 = i12;
        int iMin = (int) Math.min(complexVector.length, (2147483648L / j10) + 1);
        int i13 = (int) ((((((long) iMin) * j10) + 31) * 8) / 32);
        byte[] bArr = new byte[i13];
        int i14 = 1;
        int i15 = (1 << i12) - 1;
        int i16 = 32 - i12;
        int i17 = (i13 * 8) - i12;
        int i18 = 0;
        int i19 = i13 - 4;
        int iMin2 = Math.min(Math.max(0, i17 >> 3), i19);
        long j11 = 0;
        int i20 = 0;
        int i21 = 0;
        while (i20 <= i14) {
            int i22 = i18;
            while (i22 < iMin) {
                long jRound = Math.round(complexVector.part(i22, i20)) + j11;
                long j12 = jRound >> i12;
                int iMin3 = Math.min(Math.max(i18, i17 >> 3), i19);
                i21 = (int) (((jRound & ((long) i15)) << ((i16 - i17) + (iMin3 << 3))) | ((long) (i21 >>> ((iMin2 - iMin3) << 3))));
                FastDoubleSwar.writeIntBE(bArr, iMin3, i21);
                i17 -= i11;
                i22++;
                i12 = i11;
                iMin2 = iMin3;
                j11 = j12;
                iMin = iMin;
                i18 = 0;
            }
            i20++;
            i12 = i11;
            i14 = 1;
            i18 = 0;
        }
        return new BigInteger(i10, bArr);
    }

    private static ComplexVector[] getRootsOfUnity2(int i10) {
        ComplexVector[] complexVectorArr = new ComplexVector[i10 + 1];
        while (i10 >= 0) {
            if (i10 < 20) {
                if (ROOTS2_CACHE[i10] == null) {
                    ROOTS2_CACHE[i10] = calculateRootsOfUnity(1 << i10);
                }
                complexVectorArr[i10] = ROOTS2_CACHE[i10];
            } else {
                complexVectorArr[i10] = calculateRootsOfUnity(1 << i10);
            }
            i10 -= 2;
        }
        return complexVectorArr;
    }

    private static ComplexVector getRootsOfUnity3(int i10) {
        if (i10 >= 20) {
            return calculateRootsOfUnity(3 << i10);
        }
        if (ROOTS3_CACHE[i10] == null) {
            ROOTS3_CACHE[i10] = calculateRootsOfUnity(3 << i10);
        }
        return ROOTS3_CACHE[i10];
    }

    private static void ifft(ComplexVector complexVector, ComplexVector[] complexVectorArr) {
        int i10;
        int i11;
        int i12 = complexVector.length;
        int iNumberOfLeadingZeros = 31 - Integer.numberOfLeadingZeros(i12);
        MutableComplex mutableComplex = new MutableComplex();
        MutableComplex mutableComplex2 = new MutableComplex();
        MutableComplex mutableComplex3 = new MutableComplex();
        MutableComplex mutableComplex4 = new MutableComplex();
        MutableComplex mutableComplex5 = new MutableComplex();
        MutableComplex mutableComplex6 = new MutableComplex();
        MutableComplex mutableComplex7 = new MutableComplex();
        MutableComplex mutableComplex8 = new MutableComplex();
        int i13 = 1;
        if (iNumberOfLeadingZeros % 2 != 0) {
            for (int i14 = 0; i14 < i12; i14 += 2) {
                int i15 = i14 + 1;
                complexVector.copyInto(i15, mutableComplex3);
                complexVector.copyInto(i14, mutableComplex);
                complexVector.add(i14, mutableComplex3);
                mutableComplex.subtractInto(mutableComplex3, complexVector, i15);
            }
            i10 = 2;
        } else {
            i10 = 1;
        }
        MutableComplex mutableComplex9 = new MutableComplex();
        MutableComplex mutableComplex10 = new MutableComplex();
        while (i10 <= iNumberOfLeadingZeros) {
            ComplexVector complexVector2 = complexVectorArr[i10 - 1];
            int i16 = i13 << (i10 + 1);
            int i17 = 0;
            while (i17 < i12) {
                int i18 = i10;
                int i19 = 0;
                while (true) {
                    i11 = i17;
                    int i20 = i16 / 4;
                    if (i19 < i20) {
                        mutableComplex9.set(complexVector2, i19);
                        mutableComplex9.squareInto(mutableComplex10);
                        int i21 = i19;
                        int i22 = i11 + i21;
                        int i23 = i20 + i22;
                        ComplexVector complexVector3 = complexVector2;
                        int i24 = (i16 / 2) + i22;
                        int i25 = iNumberOfLeadingZeros;
                        int i26 = ((i16 * 3) / 4) + i22;
                        complexVector.copyInto(i22, mutableComplex);
                        complexVector.multiplyInto(i23, mutableComplex9, mutableComplex2);
                        complexVector.multiplyInto(i24, mutableComplex10, mutableComplex3);
                        complexVector.multiplyConjugateInto(i26, mutableComplex9, mutableComplex4);
                        mutableComplex.addInto(mutableComplex2, mutableComplex5);
                        mutableComplex5.add(mutableComplex3);
                        mutableComplex5.add(mutableComplex4);
                        mutableComplex.addTimesIInto(mutableComplex2, mutableComplex6);
                        mutableComplex6.subtract(mutableComplex3);
                        mutableComplex6.subtractTimesI(mutableComplex4);
                        mutableComplex.subtractInto(mutableComplex2, mutableComplex7);
                        mutableComplex7.add(mutableComplex3);
                        mutableComplex7.subtract(mutableComplex4);
                        mutableComplex.subtractTimesIInto(mutableComplex2, mutableComplex8);
                        mutableComplex8.subtract(mutableComplex3);
                        mutableComplex8.addTimesI(mutableComplex4);
                        mutableComplex5.copyInto(complexVector, i22);
                        mutableComplex6.copyInto(complexVector, i23);
                        mutableComplex7.copyInto(complexVector, i24);
                        mutableComplex8.copyInto(complexVector, i26);
                        i19 = i21 + 1;
                        i17 = i11;
                        complexVector2 = complexVector3;
                        iNumberOfLeadingZeros = i25;
                    }
                }
                i17 = i11 + i16;
                i10 = i18;
            }
            i10 += 2;
            i13 = 1;
        }
        int i27 = iNumberOfLeadingZeros;
        for (int i28 = 0; i28 < i12; i28++) {
            complexVector.timesTwoToThe(i28, -i27);
        }
    }

    private static void ifftMixedRadix(ComplexVector complexVector, ComplexVector[] complexVectorArr, ComplexVector complexVector2) {
        int i10 = complexVector.length / 3;
        ComplexVector complexVector3 = new ComplexVector(complexVector, 0, i10);
        int i11 = i10 * 2;
        ComplexVector complexVector4 = new ComplexVector(complexVector, i10, i11);
        ComplexVector complexVector5 = new ComplexVector(complexVector, i11, complexVector.length);
        ifft(complexVector3, complexVectorArr);
        ifft(complexVector4, complexVectorArr);
        ifft(complexVector5, complexVectorArr);
        MutableComplex mutableComplex = new MutableComplex();
        for (int i12 = 0; i12 < complexVector.length / 4; i12++) {
            mutableComplex.set(complexVector2, i12);
            complexVector4.multiply(i12, mutableComplex);
            complexVector5.multiply(i12, mutableComplex);
            complexVector5.multiply(i12, mutableComplex);
        }
        for (int i13 = complexVector.length / 4; i13 < i10; i13++) {
            mutableComplex.set(complexVector2, i13 - (complexVector.length / 4));
            complexVector4.multiplyByIAnd(i13, mutableComplex);
            complexVector5.multiplyByIAnd(i13, mutableComplex);
            complexVector5.multiplyByIAnd(i13, mutableComplex);
        }
        fft3(complexVector3, complexVector4, complexVector5, -1, 0.3333333333333333d);
    }

    public static BigInteger multiply(BigInteger bigInteger, BigInteger bigInteger2) {
        if (bigInteger2.signum() == 0 || bigInteger.signum() == 0) {
            return BigInteger.ZERO;
        }
        if (bigInteger2 == bigInteger) {
            return square(bigInteger2);
        }
        int iBitLength = bigInteger.bitLength();
        int iBitLength2 = bigInteger2.bitLength();
        if (((long) iBitLength) + ((long) iBitLength2) <= 2147483648L) {
            return (iBitLength <= TOOM_COOK_THRESHOLD || iBitLength2 <= TOOM_COOK_THRESHOLD || (iBitLength <= FFT_THRESHOLD && iBitLength2 <= FFT_THRESHOLD)) ? bigInteger.multiply(bigInteger2) : multiplyFft(bigInteger, bigInteger2);
        }
        throw new ArithmeticException("BigInteger would overflow supported range");
    }

    public static BigInteger multiplyFft(BigInteger bigInteger, BigInteger bigInteger2) {
        int iSignum = bigInteger2.signum() * bigInteger.signum();
        if (bigInteger.signum() < 0) {
            bigInteger = bigInteger.negate();
        }
        byte[] byteArray = bigInteger.toByteArray();
        if (bigInteger2.signum() < 0) {
            bigInteger2 = bigInteger2.negate();
        }
        byte[] byteArray2 = bigInteger2.toByteArray();
        int iMax = Math.max(byteArray.length, byteArray2.length) * 8;
        int iBitsPerFftPoint = bitsPerFftPoint(iMax);
        int i10 = ((iMax + iBitsPerFftPoint) - 1) / iBitsPerFftPoint;
        int i11 = i10 + 1;
        int iNumberOfLeadingZeros = Integer.numberOfLeadingZeros(i10);
        int i12 = 32 - iNumberOfLeadingZeros;
        int i13 = 1 << i12;
        int i14 = (i13 * 3) / 4;
        if (i11 >= i14 || i12 <= 3) {
            ComplexVector[] rootsOfUnity2 = getRootsOfUnity2(i12);
            ComplexVector fftVector = toFftVector(byteArray, i13, iBitsPerFftPoint);
            fftVector.applyWeights(rootsOfUnity2[i12]);
            fft(fftVector, rootsOfUnity2);
            ComplexVector fftVector2 = toFftVector(byteArray2, i13, iBitsPerFftPoint);
            fftVector2.applyWeights(rootsOfUnity2[i12]);
            fft(fftVector2, rootsOfUnity2);
            fftVector.multiplyPointwise(fftVector2);
            ifft(fftVector, rootsOfUnity2);
            fftVector.applyInverseWeights(rootsOfUnity2[i12]);
            return fromFftVector(fftVector, iSignum, iBitsPerFftPoint);
        }
        int i15 = 30 - iNumberOfLeadingZeros;
        ComplexVector[] rootsOfUnity3 = getRootsOfUnity2(i15);
        ComplexVector rootsOfUnity4 = getRootsOfUnity3(i15);
        ComplexVector rootsOfUnity5 = getRootsOfUnity3(28 - iNumberOfLeadingZeros);
        ComplexVector fftVector3 = toFftVector(byteArray, i14, iBitsPerFftPoint);
        fftVector3.applyWeights(rootsOfUnity4);
        fftMixedRadix(fftVector3, rootsOfUnity3, rootsOfUnity5);
        ComplexVector fftVector4 = toFftVector(byteArray2, i14, iBitsPerFftPoint);
        fftVector4.applyWeights(rootsOfUnity4);
        fftMixedRadix(fftVector4, rootsOfUnity3, rootsOfUnity5);
        fftVector3.multiplyPointwise(fftVector4);
        ifftMixedRadix(fftVector3, rootsOfUnity3, rootsOfUnity5);
        fftVector3.applyInverseWeights(rootsOfUnity4);
        return fromFftVector(fftVector3, iSignum, iBitsPerFftPoint);
    }

    public static BigInteger square(BigInteger bigInteger) {
        if (bigInteger.signum() == 0) {
            return BigInteger.ZERO;
        }
        return bigInteger.bitLength() < FFT_THRESHOLD ? bigInteger.multiply(bigInteger) : squareFft(bigInteger);
    }

    public static BigInteger squareFft(BigInteger bigInteger) {
        byte[] byteArray = bigInteger.toByteArray();
        int length = byteArray.length * 8;
        int iBitsPerFftPoint = bitsPerFftPoint(length);
        int i10 = ((length + iBitsPerFftPoint) - 1) / iBitsPerFftPoint;
        int i11 = i10 + 1;
        int iNumberOfLeadingZeros = Integer.numberOfLeadingZeros(i10);
        int i12 = 32 - iNumberOfLeadingZeros;
        int i13 = 1 << i12;
        int i14 = (i13 * 3) / 4;
        if (i11 >= i14) {
            ComplexVector fftVector = toFftVector(byteArray, i13, iBitsPerFftPoint);
            ComplexVector[] rootsOfUnity2 = getRootsOfUnity2(i12);
            fftVector.applyWeights(rootsOfUnity2[i12]);
            fft(fftVector, rootsOfUnity2);
            fftVector.squarePointwise();
            ifft(fftVector, rootsOfUnity2);
            fftVector.applyInverseWeights(rootsOfUnity2[i12]);
            return fromFftVector(fftVector, 1, iBitsPerFftPoint);
        }
        ComplexVector fftVector2 = toFftVector(byteArray, i14, iBitsPerFftPoint);
        int i15 = 30 - iNumberOfLeadingZeros;
        ComplexVector[] rootsOfUnity3 = getRootsOfUnity2(i15);
        ComplexVector rootsOfUnity4 = getRootsOfUnity3(i15);
        ComplexVector rootsOfUnity5 = getRootsOfUnity3(28 - iNumberOfLeadingZeros);
        fftVector2.applyWeights(rootsOfUnity4);
        fftMixedRadix(fftVector2, rootsOfUnity3, rootsOfUnity5);
        fftVector2.squarePointwise();
        ifftMixedRadix(fftVector2, rootsOfUnity3, rootsOfUnity5);
        fftVector2.applyInverseWeights(rootsOfUnity4);
        return fromFftVector(fftVector2, 1, iBitsPerFftPoint);
    }

    public static ComplexVector toFftVector(byte[] bArr, int i10, int i11) {
        ComplexVector complexVector = new ComplexVector(i10);
        if (bArr.length < 4) {
            byte[] bArr2 = new byte[4];
            System.arraycopy(bArr, 0, bArr2, 4 - bArr.length, bArr.length);
            bArr = bArr2;
        }
        int i12 = 1 << i11;
        int i13 = i12 / 2;
        int i14 = i12 - 1;
        int i15 = 32 - i11;
        int length = (bArr.length * 8) - i11;
        int i16 = 0;
        int i17 = 0;
        while (length > (-i11)) {
            int iMin = Math.min(Math.max(0, length >> 3), bArr.length - 4);
            int intBE = ((FastDoubleSwar.readIntBE(bArr, iMin) >>> ((i15 - length) + (iMin << 3))) & i14) + i16;
            i16 = (i13 - intBE) >>> 31;
            complexVector.real(i17, intBE - ((-i16) & i12));
            i17++;
            length -= i11;
        }
        if (i16 > 0) {
            complexVector.real(i17, i16);
        }
        return complexVector;
    }

    public static final class MutableComplex {
        double imag;
        double real;

        public void add(MutableComplex mutableComplex) {
            this.real += mutableComplex.real;
            this.imag += mutableComplex.imag;
        }

        public void addInto(MutableComplex mutableComplex, MutableComplex mutableComplex2) {
            mutableComplex2.real = this.real + mutableComplex.real;
            mutableComplex2.imag = this.imag + mutableComplex.imag;
        }

        public void addTimesI(MutableComplex mutableComplex) {
            this.real -= mutableComplex.imag;
            this.imag += mutableComplex.real;
        }

        public void addTimesIInto(MutableComplex mutableComplex, MutableComplex mutableComplex2) {
            mutableComplex2.real = this.real - mutableComplex.imag;
            mutableComplex2.imag = this.imag + mutableComplex.real;
        }

        public void copyInto(ComplexVector complexVector, int i10) {
            complexVector.real(i10, this.real);
            complexVector.imag(i10, this.imag);
        }

        public void multiply(MutableComplex mutableComplex) {
            double d10 = this.real;
            this.real = FastDoubleSwar.fma(d10, mutableComplex.real, (-this.imag) * mutableComplex.imag);
            this.imag = FastDoubleSwar.fma(d10, mutableComplex.imag, this.imag * mutableComplex.real);
        }

        public void multiplyConjugate(MutableComplex mutableComplex) {
            double d10 = this.real;
            this.real = FastDoubleSwar.fma(d10, mutableComplex.real, this.imag * mutableComplex.imag);
            this.imag = FastDoubleSwar.fma(-d10, mutableComplex.imag, this.imag * mutableComplex.real);
        }

        public void set(ComplexVector complexVector, int i10) {
            this.real = complexVector.real(i10);
            this.imag = complexVector.imag(i10);
        }

        public void squareInto(MutableComplex mutableComplex) {
            double d10 = this.real;
            double d11 = this.imag;
            mutableComplex.real = FastDoubleSwar.fma(d10, d10, (-d11) * d11);
            mutableComplex.imag = this.real * 2.0d * this.imag;
        }

        public void subtract(MutableComplex mutableComplex) {
            this.real -= mutableComplex.real;
            this.imag -= mutableComplex.imag;
        }

        public void subtractInto(MutableComplex mutableComplex, MutableComplex mutableComplex2) {
            mutableComplex2.real = this.real - mutableComplex.real;
            mutableComplex2.imag = this.imag - mutableComplex.imag;
        }

        public void subtractTimesI(MutableComplex mutableComplex) {
            this.real += mutableComplex.imag;
            this.imag -= mutableComplex.real;
        }

        public void subtractTimesIInto(MutableComplex mutableComplex, MutableComplex mutableComplex2) {
            mutableComplex2.real = this.real + mutableComplex.imag;
            mutableComplex2.imag = this.imag - mutableComplex.real;
        }

        public void add(ComplexVector complexVector, int i10) {
            this.real += complexVector.real(i10);
            this.imag += complexVector.imag(i10);
        }

        public void addTimesI(ComplexVector complexVector, int i10) {
            this.real -= complexVector.imag(i10);
            this.imag += complexVector.real(i10);
        }

        public void subtract(ComplexVector complexVector, int i10) {
            this.real -= complexVector.real(i10);
            this.imag -= complexVector.imag(i10);
        }

        public void subtractInto(MutableComplex mutableComplex, ComplexVector complexVector, int i10) {
            complexVector.real(i10, this.real - mutableComplex.real);
            complexVector.imag(i10, this.imag - mutableComplex.imag);
        }

        public void subtractTimesI(ComplexVector complexVector, int i10) {
            this.real += complexVector.imag(i10);
            this.imag -= complexVector.real(i10);
        }
    }
}

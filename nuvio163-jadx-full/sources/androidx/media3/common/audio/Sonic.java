package androidx.media3.common.audio;

import androidx.media3.common.util.Assertions;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ShortBuffer;
import java.util.Arrays;

/* JADX INFO: loaded from: classes3.dex */
final class Sonic {
    private static final int AMDF_FREQUENCY = 4000;
    private static final int BYTES_PER_SAMPLE = 2;
    private static final int MAXIMUM_PITCH = 400;
    private static final int MINIMUM_PITCH = 65;
    private static final float MINIMUM_SLOWDOWN_RATE = 0.99999f;
    private static final float MINIMUM_SPEEDUP_RATE = 1.00001f;
    private double accumulatedSpeedAdjustmentError;
    private final int channelCount;
    private final short[] downSampleBuffer;
    private short[] inputBuffer;
    private int inputFrameCount;
    private final int inputSampleRateHz;
    private int maxDiff;
    private final int maxPeriod;
    private final int maxRequiredFrameCount;
    private int minDiff;
    private final int minPeriod;
    private int newRatePosition;
    private int oldRatePosition;
    private short[] outputBuffer;
    private int outputFrameCount;
    private final float pitch;
    private short[] pitchBuffer;
    private int pitchFrameCount;
    private int prevMinDiff;
    private int prevPeriod;
    private final float rate;
    private int remainingInputToCopyFrameCount;
    private final float speed;

    public Sonic(int i10, int i11, float f10, float f11, int i12) {
        this.inputSampleRateHz = i10;
        this.channelCount = i11;
        this.speed = f10;
        this.pitch = f11;
        this.rate = i10 / i12;
        this.minPeriod = i10 / MAXIMUM_PITCH;
        int i13 = i10 / MINIMUM_PITCH;
        this.maxPeriod = i13;
        int i14 = i13 * 2;
        this.maxRequiredFrameCount = i14;
        this.downSampleBuffer = new short[i14];
        this.inputBuffer = new short[i14 * i11];
        this.outputBuffer = new short[i14 * i11];
        this.pitchBuffer = new short[i14 * i11];
    }

    private void adjustRate(float f10, int i10) {
        int i11;
        int i12;
        if (this.outputFrameCount == i10) {
            return;
        }
        int i13 = this.inputSampleRateHz;
        long j10 = (long) (i13 / f10);
        long j11 = i13;
        while (j10 != 0 && j11 != 0 && j10 % 2 == 0 && j11 % 2 == 0) {
            j10 /= 2;
            j11 /= 2;
        }
        moveNewSamplesToPitchBuffer(i10);
        int i14 = 0;
        while (true) {
            int i15 = this.pitchFrameCount;
            if (i14 >= i15 - 1) {
                removePitchFrames(i15 - 1);
                return;
            }
            while (true) {
                i11 = this.oldRatePosition;
                long j12 = ((long) (i11 + 1)) * j10;
                i12 = this.newRatePosition;
                if (j12 <= ((long) i12) * j11) {
                    break;
                }
                this.outputBuffer = ensureSpaceForAdditionalFrames(this.outputBuffer, this.outputFrameCount, 1);
                int i16 = 0;
                while (true) {
                    int i17 = this.channelCount;
                    if (i16 < i17) {
                        this.outputBuffer[(this.outputFrameCount * i17) + i16] = interpolate(this.pitchBuffer, (i17 * i14) + i16, j11, j10);
                        i16++;
                    }
                }
                this.newRatePosition++;
                this.outputFrameCount++;
            }
            int i18 = i11 + 1;
            this.oldRatePosition = i18;
            if (i18 == j11) {
                this.oldRatePosition = 0;
                Assertions.checkState(((long) i12) == j10);
                this.newRatePosition = 0;
            }
            i14++;
        }
    }

    public static long calculateAccumulatedTruncationErrorForResampling(BigDecimal bigDecimal, BigDecimal bigDecimal2, BigDecimal bigDecimal3) {
        RoundingMode roundingMode = RoundingMode.HALF_EVEN;
        BigDecimal bigDecimalDivide = bigDecimal.divide(bigDecimal2, 20, roundingMode);
        BigDecimal bigDecimalDivide2 = bigDecimal2.divide(bigDecimal3, 20, roundingMode);
        RoundingMode roundingMode2 = RoundingMode.FLOOR;
        return bigDecimalDivide.multiply(bigDecimalDivide2.subtract(bigDecimalDivide2.setScale(0, roundingMode2))).setScale(0, roundingMode2).longValueExact();
    }

    private void changeSpeed(double d10) {
        Sonic sonic;
        double d11;
        int i10 = this.inputFrameCount;
        if (i10 < this.maxRequiredFrameCount) {
            return;
        }
        int iInsertPitchPeriod = 0;
        while (true) {
            if (this.remainingInputToCopyFrameCount > 0) {
                iInsertPitchPeriod += copyInputToOutput(iInsertPitchPeriod);
                sonic = this;
                d11 = d10;
            } else {
                int iFindPitchPeriod = findPitchPeriod(this.inputBuffer, iInsertPitchPeriod);
                if (d10 > 1.0d) {
                    sonic = this;
                    d11 = d10;
                    iInsertPitchPeriod = iFindPitchPeriod + sonic.skipPitchPeriod(this.inputBuffer, iInsertPitchPeriod, d11, iFindPitchPeriod) + iInsertPitchPeriod;
                } else {
                    sonic = this;
                    d11 = d10;
                    iInsertPitchPeriod += sonic.insertPitchPeriod(sonic.inputBuffer, iInsertPitchPeriod, d11, iFindPitchPeriod);
                }
            }
            if (sonic.maxRequiredFrameCount + iInsertPitchPeriod > i10) {
                removeProcessedInputFrames(iInsertPitchPeriod);
                return;
            }
            d10 = d11;
        }
    }

    private int copyInputToOutput(int i10) {
        int iMin = Math.min(this.maxRequiredFrameCount, this.remainingInputToCopyFrameCount);
        copyToOutput(this.inputBuffer, i10, iMin);
        this.remainingInputToCopyFrameCount -= iMin;
        return iMin;
    }

    private void copyToOutput(short[] sArr, int i10, int i11) {
        short[] sArrEnsureSpaceForAdditionalFrames = ensureSpaceForAdditionalFrames(this.outputBuffer, this.outputFrameCount, i11);
        this.outputBuffer = sArrEnsureSpaceForAdditionalFrames;
        int i12 = this.channelCount;
        System.arraycopy(sArr, i10 * i12, sArrEnsureSpaceForAdditionalFrames, this.outputFrameCount * i12, i12 * i11);
        this.outputFrameCount += i11;
    }

    private void downSampleInput(short[] sArr, int i10, int i11) {
        int i12 = this.maxRequiredFrameCount / i11;
        int i13 = this.channelCount;
        int i14 = i11 * i13;
        int i15 = i10 * i13;
        for (int i16 = 0; i16 < i12; i16++) {
            int i17 = 0;
            for (int i18 = 0; i18 < i14; i18++) {
                i17 += sArr[(i16 * i14) + i15 + i18];
            }
            this.downSampleBuffer[i16] = (short) (i17 / i14);
        }
    }

    private short[] ensureSpaceForAdditionalFrames(short[] sArr, int i10, int i11) {
        int length = sArr.length;
        int i12 = this.channelCount;
        int i13 = length / i12;
        return i10 + i11 <= i13 ? sArr : Arrays.copyOf(sArr, (((i13 * 3) / 2) + i11) * i12);
    }

    private int findPitchPeriod(short[] sArr, int i10) {
        int iFindPitchPeriodInRange;
        int i11 = this.inputSampleRateHz;
        int i12 = i11 > 4000 ? i11 / 4000 : 1;
        if (this.channelCount == 1 && i12 == 1) {
            iFindPitchPeriodInRange = findPitchPeriodInRange(sArr, i10, this.minPeriod, this.maxPeriod);
        } else {
            downSampleInput(sArr, i10, i12);
            int iFindPitchPeriodInRange2 = findPitchPeriodInRange(this.downSampleBuffer, 0, this.minPeriod / i12, this.maxPeriod / i12);
            if (i12 != 1) {
                int i13 = iFindPitchPeriodInRange2 * i12;
                int i14 = i12 * 4;
                int i15 = i13 - i14;
                int i16 = i13 + i14;
                int i17 = this.minPeriod;
                if (i15 < i17) {
                    i15 = i17;
                }
                int i18 = this.maxPeriod;
                if (i16 > i18) {
                    i16 = i18;
                }
                if (this.channelCount == 1) {
                    iFindPitchPeriodInRange = findPitchPeriodInRange(sArr, i10, i15, i16);
                } else {
                    downSampleInput(sArr, i10, 1);
                    iFindPitchPeriodInRange = findPitchPeriodInRange(this.downSampleBuffer, 0, i15, i16);
                }
            } else {
                iFindPitchPeriodInRange = iFindPitchPeriodInRange2;
            }
        }
        int i19 = previousPeriodBetter(this.minDiff, this.maxDiff) ? this.prevPeriod : iFindPitchPeriodInRange;
        this.prevMinDiff = this.minDiff;
        this.prevPeriod = iFindPitchPeriodInRange;
        return i19;
    }

    private int findPitchPeriodInRange(short[] sArr, int i10, int i11, int i12) {
        int i13 = i10 * this.channelCount;
        int i14 = 255;
        int i15 = 1;
        int i16 = 0;
        int i17 = 0;
        while (i11 <= i12) {
            int iAbs = 0;
            for (int i18 = 0; i18 < i11; i18++) {
                iAbs += Math.abs(sArr[i13 + i18] - sArr[(i13 + i11) + i18]);
            }
            if (iAbs * i16 < i15 * i11) {
                i16 = i11;
                i15 = iAbs;
            }
            if (iAbs * i14 > i17 * i11) {
                i14 = i11;
                i17 = iAbs;
            }
            i11++;
        }
        this.minDiff = i15 / i16;
        this.maxDiff = i17 / i14;
        return i16;
    }

    public static long getExpectedFrameCountAfterProcessorApplied(int i10, int i11, float f10, float f11, long j10) {
        float f12 = (i10 / i11) * f11;
        double d10 = f10 / f11;
        BigDecimal bigDecimal = new BigDecimal(String.valueOf(f12));
        BigDecimal bigDecimalValueOf = BigDecimal.valueOf(j10);
        if (d10 > 1.0000100135803223d || d10 < 0.9999899864196777d) {
            bigDecimalValueOf = bigDecimalValueOf.divide(BigDecimal.valueOf(d10), RoundingMode.HALF_EVEN);
        }
        return f12 == 1.0f ? bigDecimalValueOf.longValueExact() : bigDecimalValueOf.divide(bigDecimal, RoundingMode.HALF_EVEN).longValueExact() - calculateAccumulatedTruncationErrorForResampling(bigDecimalValueOf, BigDecimal.valueOf(i10), bigDecimal);
    }

    public static long getExpectedInputFrameCountForOutputFrameCount(int i10, int i11, float f10, float f11, long j10) {
        long frameCountBeforeResamplingForOutputCount = getFrameCountBeforeResamplingForOutputCount(BigDecimal.valueOf(i10), new BigDecimal(String.valueOf((i10 / i11) * f11)), BigDecimal.valueOf(j10));
        double d10 = f10 / f11;
        return (d10 > 1.0000100135803223d || d10 < 0.9999899864196777d) ? BigDecimal.valueOf(frameCountBeforeResamplingForOutputCount).multiply(BigDecimal.valueOf(d10)).setScale(0, RoundingMode.FLOOR).longValueExact() : frameCountBeforeResamplingForOutputCount;
    }

    private static long getFrameCountBeforeResamplingForOutputCount(BigDecimal bigDecimal, BigDecimal bigDecimal2, BigDecimal bigDecimal3) {
        RoundingMode roundingMode = RoundingMode.FLOOR;
        return bigDecimal.multiply(bigDecimal3).divide(bigDecimal.divide(bigDecimal2, 0, roundingMode), 0, roundingMode).longValueExact();
    }

    private int insertPitchPeriod(short[] sArr, int i10, double d10, int i11) {
        int i12;
        if (d10 < 0.5d) {
            double d11 = ((((double) i11) * d10) / (1.0d - d10)) + this.accumulatedSpeedAdjustmentError;
            int iRound = (int) Math.round(d11);
            this.accumulatedSpeedAdjustmentError = d11 - ((double) iRound);
            i12 = iRound;
        } else {
            double d12 = ((((2.0d * d10) - 1.0d) * ((double) i11)) / (1.0d - d10)) + this.accumulatedSpeedAdjustmentError;
            int iRound2 = (int) Math.round(d12);
            this.remainingInputToCopyFrameCount = iRound2;
            this.accumulatedSpeedAdjustmentError = d12 - ((double) iRound2);
            i12 = i11;
        }
        int i13 = i11 + i12;
        short[] sArrEnsureSpaceForAdditionalFrames = ensureSpaceForAdditionalFrames(this.outputBuffer, this.outputFrameCount, i13);
        this.outputBuffer = sArrEnsureSpaceForAdditionalFrames;
        int i14 = this.channelCount;
        System.arraycopy(sArr, i10 * i14, sArrEnsureSpaceForAdditionalFrames, this.outputFrameCount * i14, i14 * i11);
        overlapAdd(i12, this.channelCount, this.outputBuffer, this.outputFrameCount + i11, sArr, i10 + i11, sArr, i10);
        this.outputFrameCount += i13;
        return i12;
    }

    private short interpolate(short[] sArr, int i10, long j10, long j11) {
        short s2 = sArr[i10];
        short s4 = sArr[i10 + this.channelCount];
        long j12 = ((long) this.newRatePosition) * j10;
        int i11 = this.oldRatePosition;
        long j13 = ((long) i11) * j11;
        long j14 = ((long) (i11 + 1)) * j11;
        long j15 = j14 - j12;
        long j16 = j14 - j13;
        return (short) ((((j16 - j15) * ((long) s4)) + (((long) s2) * j15)) / j16);
    }

    private void moveNewSamplesToPitchBuffer(int i10) {
        int i11 = this.outputFrameCount - i10;
        short[] sArrEnsureSpaceForAdditionalFrames = ensureSpaceForAdditionalFrames(this.pitchBuffer, this.pitchFrameCount, i11);
        this.pitchBuffer = sArrEnsureSpaceForAdditionalFrames;
        short[] sArr = this.outputBuffer;
        int i12 = this.channelCount;
        System.arraycopy(sArr, i10 * i12, sArrEnsureSpaceForAdditionalFrames, this.pitchFrameCount * i12, i12 * i11);
        this.outputFrameCount = i10;
        this.pitchFrameCount += i11;
    }

    private static void overlapAdd(int i10, int i11, short[] sArr, int i12, short[] sArr2, int i13, short[] sArr3, int i14) {
        for (int i15 = 0; i15 < i11; i15++) {
            int i16 = (i12 * i11) + i15;
            int i17 = (i14 * i11) + i15;
            int i18 = (i13 * i11) + i15;
            for (int i19 = 0; i19 < i10; i19++) {
                sArr[i16] = (short) (((sArr3[i17] * i19) + ((i10 - i19) * sArr2[i18])) / i10);
                i16 += i11;
                i18 += i11;
                i17 += i11;
            }
        }
    }

    private boolean previousPeriodBetter(int i10, int i11) {
        return i10 != 0 && this.prevPeriod != 0 && i11 <= i10 * 3 && i10 * 2 > this.prevMinDiff * 3;
    }

    private void processStreamInput() {
        int i10 = this.outputFrameCount;
        float f10 = this.speed;
        float f11 = this.pitch;
        double d10 = f10 / f11;
        float f12 = this.rate * f11;
        if (d10 > 1.0000100135803223d || d10 < 0.9999899864196777d) {
            changeSpeed(d10);
        } else {
            copyToOutput(this.inputBuffer, 0, this.inputFrameCount);
            this.inputFrameCount = 0;
        }
        if (f12 != 1.0f) {
            adjustRate(f12, i10);
        }
    }

    private void removePitchFrames(int i10) {
        if (i10 == 0) {
            return;
        }
        short[] sArr = this.pitchBuffer;
        int i11 = this.channelCount;
        System.arraycopy(sArr, i10 * i11, sArr, 0, (this.pitchFrameCount - i10) * i11);
        this.pitchFrameCount -= i10;
    }

    private void removeProcessedInputFrames(int i10) {
        int i11 = this.inputFrameCount - i10;
        short[] sArr = this.inputBuffer;
        int i12 = this.channelCount;
        System.arraycopy(sArr, i10 * i12, sArr, 0, i12 * i11);
        this.inputFrameCount = i11;
    }

    private int skipPitchPeriod(short[] sArr, int i10, double d10, int i11) {
        int i12;
        if (d10 >= 2.0d) {
            double d11 = (((double) i11) / (d10 - 1.0d)) + this.accumulatedSpeedAdjustmentError;
            int iRound = (int) Math.round(d11);
            this.accumulatedSpeedAdjustmentError = d11 - ((double) iRound);
            i12 = iRound;
        } else {
            double d12 = (((2.0d - d10) * ((double) i11)) / (d10 - 1.0d)) + this.accumulatedSpeedAdjustmentError;
            int iRound2 = (int) Math.round(d12);
            this.remainingInputToCopyFrameCount = iRound2;
            this.accumulatedSpeedAdjustmentError = d12 - ((double) iRound2);
            i12 = i11;
        }
        short[] sArrEnsureSpaceForAdditionalFrames = ensureSpaceForAdditionalFrames(this.outputBuffer, this.outputFrameCount, i12);
        this.outputBuffer = sArrEnsureSpaceForAdditionalFrames;
        overlapAdd(i12, this.channelCount, sArrEnsureSpaceForAdditionalFrames, this.outputFrameCount, sArr, i10, sArr, i10 + i11);
        this.outputFrameCount += i12;
        return i12;
    }

    public void flush() {
        this.inputFrameCount = 0;
        this.outputFrameCount = 0;
        this.pitchFrameCount = 0;
        this.oldRatePosition = 0;
        this.newRatePosition = 0;
        this.remainingInputToCopyFrameCount = 0;
        this.prevPeriod = 0;
        this.prevMinDiff = 0;
        this.minDiff = 0;
        this.maxDiff = 0;
        this.accumulatedSpeedAdjustmentError = 0.0d;
    }

    public void getOutput(ShortBuffer shortBuffer) {
        Assertions.checkState(this.outputFrameCount >= 0);
        int iMin = Math.min(shortBuffer.remaining() / this.channelCount, this.outputFrameCount);
        shortBuffer.put(this.outputBuffer, 0, this.channelCount * iMin);
        int i10 = this.outputFrameCount - iMin;
        this.outputFrameCount = i10;
        short[] sArr = this.outputBuffer;
        int i11 = this.channelCount;
        System.arraycopy(sArr, iMin * i11, sArr, 0, i10 * i11);
    }

    public int getOutputSize() {
        Assertions.checkState(this.outputFrameCount >= 0);
        return this.outputFrameCount * this.channelCount * 2;
    }

    public int getPendingInputBytes() {
        return this.inputFrameCount * this.channelCount * 2;
    }

    public void queueEndOfStream() {
        int i10;
        int i11 = this.inputFrameCount;
        float f10 = this.speed;
        float f11 = this.pitch;
        double d10 = f10 / f11;
        double d11 = this.rate * f11;
        int i12 = this.remainingInputToCopyFrameCount;
        int i13 = this.outputFrameCount + ((int) ((((((((double) (i11 - i12)) / d10) + ((double) i12)) + this.accumulatedSpeedAdjustmentError) + ((double) this.pitchFrameCount)) / d11) + 0.5d));
        this.accumulatedSpeedAdjustmentError = 0.0d;
        this.inputBuffer = ensureSpaceForAdditionalFrames(this.inputBuffer, i11, (this.maxRequiredFrameCount * 2) + i11);
        int i14 = 0;
        while (true) {
            i10 = this.maxRequiredFrameCount;
            int i15 = this.channelCount;
            if (i14 >= i10 * 2 * i15) {
                break;
            }
            this.inputBuffer[(i15 * i11) + i14] = 0;
            i14++;
        }
        this.inputFrameCount = (i10 * 2) + this.inputFrameCount;
        processStreamInput();
        if (this.outputFrameCount > i13) {
            this.outputFrameCount = Math.max(i13, 0);
        }
        this.inputFrameCount = 0;
        this.remainingInputToCopyFrameCount = 0;
        this.pitchFrameCount = 0;
    }

    public void queueInput(ShortBuffer shortBuffer) {
        int iRemaining = shortBuffer.remaining();
        int i10 = this.channelCount;
        int i11 = iRemaining / i10;
        short[] sArrEnsureSpaceForAdditionalFrames = ensureSpaceForAdditionalFrames(this.inputBuffer, this.inputFrameCount, i11);
        this.inputBuffer = sArrEnsureSpaceForAdditionalFrames;
        shortBuffer.get(sArrEnsureSpaceForAdditionalFrames, this.inputFrameCount * this.channelCount, ((i10 * i11) * 2) / 2);
        this.inputFrameCount += i11;
        processStreamInput();
    }
}

package com.fasterxml.jackson.core.io.doubleparser;

import androidx.media3.common.C;
import androidx.media3.exoplayer.MediaPeriodQueue;
import androidx.media3.exoplayer.Renderer;
import androidx.media3.exoplayer.audio.SilenceSkippingAudioProcessor;
import java.math.BigInteger;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import okhttp3.internal.connection.RealConnection;

/* JADX INFO: loaded from: classes3.dex */
class FastIntegerMath {
    public static final BigInteger FIVE = BigInteger.valueOf(5);
    static final BigInteger TEN_POW_16 = BigInteger.valueOf(10000000000000000L);
    static final BigInteger FIVE_POW_16 = BigInteger.valueOf(152587890625L);
    private static final BigInteger[] SMALL_POWERS_OF_TEN = {BigInteger.ONE, BigInteger.TEN, BigInteger.valueOf(100), BigInteger.valueOf(1000), BigInteger.valueOf(Renderer.DEFAULT_DURATION_TO_PROGRESS_US), BigInteger.valueOf(SilenceSkippingAudioProcessor.DEFAULT_MINIMUM_SILENCE_DURATION_US), BigInteger.valueOf(1000000), BigInteger.valueOf(10000000), BigInteger.valueOf(100000000), BigInteger.valueOf(C.NANOS_PER_SECOND), BigInteger.valueOf(RealConnection.IDLE_CONNECTION_HEALTHY_NS), BigInteger.valueOf(100000000000L), BigInteger.valueOf(MediaPeriodQueue.INITIAL_RENDERER_POSITION_OFFSET_US), BigInteger.valueOf(10000000000000L), BigInteger.valueOf(100000000000000L), BigInteger.valueOf(1000000000000000L)};

    private FastIntegerMath() {
    }

    public static BigInteger computePowerOfTen(NavigableMap<Integer, BigInteger> navigableMap, int i10) {
        BigInteger[] bigIntegerArr = SMALL_POWERS_OF_TEN;
        if (i10 < bigIntegerArr.length) {
            return bigIntegerArr[i10];
        }
        if (navigableMap == null) {
            return FIVE.pow(i10).shiftLeft(i10);
        }
        Map.Entry<Integer, BigInteger> entryFloorEntry = navigableMap.floorEntry(Integer.valueOf(i10));
        Integer key = entryFloorEntry.getKey();
        return key.intValue() == i10 ? entryFloorEntry.getValue() : FftMultiplier.multiply(entryFloorEntry.getValue(), computePowerOfTen(navigableMap, i10 - key.intValue()));
    }

    public static BigInteger computeTenRaisedByNFloor16Recursive(NavigableMap<Integer, BigInteger> navigableMap, int i10) {
        int i11 = i10 & (-16);
        Map.Entry<Integer, BigInteger> entryFloorEntry = navigableMap.floorEntry(Integer.valueOf(i11));
        int iIntValue = entryFloorEntry.getKey().intValue();
        BigInteger value = entryFloorEntry.getValue();
        if (iIntValue == i11) {
            return value;
        }
        int i12 = i11 - iIntValue;
        BigInteger bigIntegerComputeTenRaisedByNFloor16Recursive = navigableMap.get(Integer.valueOf(i12));
        if (bigIntegerComputeTenRaisedByNFloor16Recursive == null) {
            bigIntegerComputeTenRaisedByNFloor16Recursive = computeTenRaisedByNFloor16Recursive(navigableMap, i12);
            navigableMap.put(Integer.valueOf(i12), bigIntegerComputeTenRaisedByNFloor16Recursive);
        }
        return FftMultiplier.multiply(value, bigIntegerComputeTenRaisedByNFloor16Recursive);
    }

    public static NavigableMap<Integer, BigInteger> createPowersOfTenFloor16Map() {
        TreeMap treeMap = new TreeMap();
        treeMap.put(0, BigInteger.ONE);
        treeMap.put(16, TEN_POW_16);
        return treeMap;
    }

    public static long estimateNumBits(long j10) {
        return ((j10 * 3402) >>> 10) + 1;
    }

    public static NavigableMap<Integer, BigInteger> fillPowersOf10Floor16(int i10, int i11) {
        TreeMap treeMap = new TreeMap();
        treeMap.put(0, BigInteger.valueOf(5L));
        treeMap.put(16, FIVE_POW_16);
        fillPowersOfNFloor16Recursive(treeMap, i10, i11);
        for (Map.Entry entry : treeMap.entrySet()) {
            entry.setValue(((BigInteger) entry.getValue()).shiftLeft(((Integer) entry.getKey()).intValue()));
        }
        return treeMap;
    }

    public static void fillPowersOfNFloor16Recursive(NavigableMap<Integer, BigInteger> navigableMap, int i10, int i11) {
        if (i11 - i10 <= 18) {
            return;
        }
        int iSplitFloor16 = splitFloor16(i10, i11);
        int i12 = i11 - iSplitFloor16;
        if (navigableMap.containsKey(Integer.valueOf(i12))) {
            return;
        }
        fillPowersOfNFloor16Recursive(navigableMap, i10, iSplitFloor16);
        fillPowersOfNFloor16Recursive(navigableMap, iSplitFloor16, i11);
        navigableMap.put(Integer.valueOf(i12), computeTenRaisedByNFloor16Recursive(navigableMap, i12));
    }

    public static int splitFloor16(int i10, int i11) {
        return i11 - ((((i11 - ((i10 + i11) >>> 1)) + 15) >> 4) << 4);
    }

    public static long unsignedMultiplyHigh(long j10, long j11) {
        long j12 = j10 & 4294967295L;
        long j13 = j10 >>> 32;
        long j14 = j11 & 4294967295L;
        long j15 = j11 >>> 32;
        long j16 = j13 * j15;
        long j17 = j15 * j12;
        return j16 + ((((j13 * j14) + ((j12 * j14) >>> 32)) + (4294967295L & j17)) >>> 32) + (j17 >>> 32);
    }
}

package org.conscrypt;

import java.nio.ByteBuffer;

/* JADX INFO: loaded from: classes5.dex */
final class BufferUtils {
    private BufferUtils() {
    }

    public static void checkNotNull(ByteBuffer[] byteBufferArr) {
        for (ByteBuffer byteBuffer : byteBufferArr) {
            if (byteBuffer == null) {
                throw new IllegalArgumentException("Null buffer in array");
            }
        }
    }

    public static void consume(ByteBuffer[] byteBufferArr, int i10) {
        for (ByteBuffer byteBuffer : byteBufferArr) {
            int iMin = Math.min(byteBuffer.remaining(), i10);
            if (iMin > 0) {
                byteBuffer.position(byteBuffer.position() + iMin);
                i10 -= iMin;
                if (i10 == 0) {
                    break;
                }
            }
        }
        if (i10 > 0) {
            throw new IllegalArgumentException("toConsume > data size");
        }
    }

    public static ByteBuffer copyNoConsume(ByteBuffer[] byteBufferArr, ByteBuffer byteBuffer, int i10) {
        Preconditions.checkArgument(byteBuffer.remaining() >= i10, "Destination buffer too small");
        for (ByteBuffer byteBuffer2 : byteBufferArr) {
            int iRemaining = byteBuffer2.remaining();
            if (iRemaining > 0) {
                int iPosition = byteBuffer2.position();
                if (iRemaining <= i10) {
                    byteBuffer.put(byteBuffer2);
                    i10 -= iRemaining;
                } else {
                    int iLimit = byteBuffer2.limit();
                    byteBuffer2.limit(byteBuffer2.position() + i10);
                    byteBuffer.put(byteBuffer2);
                    byteBuffer2.limit(iLimit);
                    i10 = 0;
                }
                byteBuffer2.position(iPosition);
                if (i10 == 0) {
                    break;
                }
            }
        }
        byteBuffer.flip();
        return byteBuffer;
    }

    public static ByteBuffer getBufferLargerThan(ByteBuffer[] byteBufferArr, int i10) {
        int length = byteBufferArr.length;
        int i11 = 0;
        while (i11 < length) {
            ByteBuffer byteBuffer = byteBufferArr[i11];
            int iRemaining = byteBuffer.remaining();
            if (iRemaining > 0) {
                if (iRemaining < i10) {
                    do {
                        i11++;
                        if (i11 < length) {
                        }
                    } while (byteBufferArr[i11].remaining() <= 0);
                    return null;
                }
                return byteBuffer;
            }
            i11++;
        }
        return null;
    }

    public static long remaining(ByteBuffer[] byteBufferArr) {
        long jRemaining = 0;
        for (ByteBuffer byteBuffer : byteBufferArr) {
            jRemaining += (long) byteBuffer.remaining();
        }
        return jRemaining;
    }
}

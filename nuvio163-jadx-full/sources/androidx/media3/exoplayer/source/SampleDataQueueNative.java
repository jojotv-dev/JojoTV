package androidx.media3.exoplayer.source;

import dalvik.annotation.optimization.FastNative;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: classes3.dex */
public final class SampleDataQueueNative {
    private static final String LIBRARY_NAME = "media3_exoplayer_jni";
    private static volatile boolean isAvailable;
    private static volatile boolean loadAttempted;
    private static final ThreadLocal<BufferAddressCache> targetCache = new ThreadLocal<BufferAddressCache>() { // from class: androidx.media3.exoplayer.source.SampleDataQueueNative.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.lang.ThreadLocal
        public BufferAddressCache initialValue() {
            return new BufferAddressCache();
        }
    };

    public static final class BufferAddressCache {
        long lastAddress;
        private WeakReference<ByteBuffer> lastBufferRef;

        private BufferAddressCache() {
            this.lastBufferRef = new WeakReference<>(null);
        }
    }

    private SampleDataQueueNative() {
    }

    public static boolean copyBetweenAddresses(ByteBuffer byteBuffer, int i10, ByteBuffer byteBuffer2, int i11, int i12) {
        long directBufferAddressCached = getDirectBufferAddressCached(byteBuffer);
        long directBufferAddressCached2 = getDirectBufferAddressCached(byteBuffer2);
        if (directBufferAddressCached == 0 || directBufferAddressCached2 == 0 || !isAvailable()) {
            return false;
        }
        try {
            nativeCopyAddresses(directBufferAddressCached, i10, directBufferAddressCached2, i11, i12);
            return true;
        } catch (UnsatisfiedLinkError unused) {
            isAvailable = false;
            return false;
        }
    }

    public static boolean copyBetweenAddressesDirect(long j10, int i10, long j11, int i11, int i12) {
        if (j10 == 0 || j11 == 0 || !isAvailable()) {
            return false;
        }
        try {
            nativeCopyAddresses(j10, i10, j11, i11, i12);
            return true;
        } catch (UnsatisfiedLinkError unused) {
            isAvailable = false;
            return false;
        }
    }

    public static boolean copyBetweenDirectBuffers(ByteBuffer byteBuffer, int i10, ByteBuffer byteBuffer2, int i11, int i12) {
        if (i12 == 0) {
            return true;
        }
        if (i10 < 0 || i12 < 0 || i10 + i12 > byteBuffer.capacity() || i11 < 0 || i11 + i12 > byteBuffer2.capacity() || !isAvailable()) {
            return false;
        }
        try {
            return nativeCopyBetweenDirectBuffers(byteBuffer, i10, byteBuffer2, i11, i12);
        } catch (UnsatisfiedLinkError unused) {
            isAvailable = false;
            return false;
        }
    }

    public static boolean copyFromArray(byte[] bArr, int i10, ByteBuffer byteBuffer, int i11, int i12) {
        if (i12 == 0) {
            return true;
        }
        if (i10 < 0 || i12 < 0 || i10 + i12 > bArr.length || i11 < 0 || i11 + i12 > byteBuffer.capacity() || !isAvailable()) {
            return false;
        }
        try {
            return nativeCopyFromArray(bArr, i10, byteBuffer, i11, i12);
        } catch (UnsatisfiedLinkError unused) {
            isAvailable = false;
            return false;
        }
    }

    public static boolean copyToArray(ByteBuffer byteBuffer, int i10, byte[] bArr, int i11, int i12) {
        if (i12 == 0) {
            return true;
        }
        if (i10 < 0 || i12 < 0 || i10 + i12 > byteBuffer.capacity() || i11 < 0 || i11 + i12 > bArr.length || !isAvailable()) {
            return false;
        }
        try {
            return nativeCopyToArray(byteBuffer, i10, bArr, i11, i12);
        } catch (UnsatisfiedLinkError unused) {
            isAvailable = false;
            return false;
        }
    }

    public static long getDirectBufferAddress(ByteBuffer byteBuffer) {
        if (byteBuffer != null && byteBuffer.isDirect() && isAvailable()) {
            try {
                return nativeGetBufferAddress(byteBuffer);
            } catch (UnsatisfiedLinkError unused) {
                isAvailable = false;
            }
        }
        return 0L;
    }

    public static long getDirectBufferAddressCached(ByteBuffer byteBuffer) {
        if (byteBuffer == null) {
            return 0L;
        }
        BufferAddressCache bufferAddressCache = targetCache.get();
        if (((ByteBuffer) bufferAddressCache.lastBufferRef.get()) == byteBuffer) {
            return bufferAddressCache.lastAddress;
        }
        long directBufferAddress = getDirectBufferAddress(byteBuffer);
        bufferAddressCache.lastBufferRef = new WeakReference(byteBuffer);
        bufferAddressCache.lastAddress = directBufferAddress;
        return directBufferAddress;
    }

    private static boolean isAvailable() {
        return loadAttempted ? isAvailable : loadLibrary();
    }

    private static synchronized boolean loadLibrary() {
        if (loadAttempted) {
            return isAvailable;
        }
        loadAttempted = true;
        try {
            System.loadLibrary(LIBRARY_NAME);
            isAvailable = true;
        } catch (SecurityException | UnsatisfiedLinkError unused) {
            isAvailable = false;
        }
        return isAvailable;
    }

    @FastNative
    public static native void nativeCopyAddresses(long j10, int i10, long j11, int i11, int i12);

    @FastNative
    private static native boolean nativeCopyBetweenDirectBuffers(ByteBuffer byteBuffer, int i10, ByteBuffer byteBuffer2, int i11, int i12);

    @FastNative
    private static native boolean nativeCopyFromArray(byte[] bArr, int i10, ByteBuffer byteBuffer, int i11, int i12);

    @FastNative
    private static native boolean nativeCopyToArray(ByteBuffer byteBuffer, int i10, byte[] bArr, int i11, int i12);

    @FastNative
    private static native long nativeGetBufferAddress(ByteBuffer byteBuffer);
}

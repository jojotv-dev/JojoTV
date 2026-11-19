package androidx.media3.exoplayer.upstream;

import androidx.media3.session.legacy.PlaybackStateCompat;
import dalvik.annotation.optimization.FastNative;
import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/* JADX INFO: loaded from: classes3.dex */
public final class DefaultAllocatorNative {
    private static final int ARENA_CHUNK_SIZE = 65536;
    private static final int ARENA_MAX_CHUNKS = 512;
    private static final int ARENA_MIN_CHUNKS = 64;
    private static final String LIBRARY_NAME = "media3_exoplayer_jni";
    private static volatile boolean isAvailable;
    private static volatile boolean loadAttempted;
    private static final Queue<Allocation> arenaPool = new ConcurrentLinkedQueue();
    private static volatile long arenaBaseAddress = 0;
    private static volatile long arenaEndAddress = 0;
    private static Allocation arenaBaseAllocation = null;
    private static volatile boolean arenaInitialized = false;

    private DefaultAllocatorNative() {
    }

    public static Allocation createAllocation(int i10) {
        Allocation allocationPoll;
        if (i10 == 65536) {
            if (!arenaInitialized) {
                initializeArena();
            }
            if (arenaBaseAddress != 0 && (allocationPoll = arenaPool.poll()) != null) {
                return allocationPoll;
            }
        }
        return createAllocationDirect(i10);
    }

    private static Allocation createAllocationDirect(int i10) {
        if (!isAvailable()) {
            return null;
        }
        try {
            return nativeCreateAllocation(i10);
        } catch (UnsatisfiedLinkError unused) {
            isAvailable = false;
            return null;
        }
    }

    public static void freeAllocation(Allocation allocation) {
        long j10 = allocation.nativeHandle;
        if (j10 == 0) {
            return;
        }
        if (j10 >= arenaBaseAddress && j10 < arenaEndAddress) {
            allocation.nativeHandle = 0L;
            arenaPool.offer(new Allocation(allocation.buffer, allocation.offset, j10));
        } else {
            allocation.nativeHandle = 0L;
            try {
                nativeFreeAllocation(j10);
            } catch (UnsatisfiedLinkError unused) {
                isAvailable = false;
            }
        }
    }

    private static synchronized void initializeArena() {
        Allocation allocationNativeCreateAllocation;
        if (arenaInitialized) {
            return;
        }
        arenaInitialized = true;
        if (isAvailable()) {
            int i10 = 512;
            while (true) {
                allocationNativeCreateAllocation = null;
                if (i10 < 64) {
                    break;
                }
                try {
                    allocationNativeCreateAllocation = nativeCreateAllocation(i10 * 65536);
                } catch (Exception | UnsatisfiedLinkError unused) {
                }
                if (allocationNativeCreateAllocation != null && allocationNativeCreateAllocation.buffer != null) {
                    break;
                } else {
                    i10 >>= 1;
                }
            }
            if (allocationNativeCreateAllocation == null) {
                return;
            }
            arenaBaseAllocation = allocationNativeCreateAllocation;
            arenaBaseAddress = allocationNativeCreateAllocation.nativeHandle;
            arenaEndAddress = (((long) i10) * PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH) + arenaBaseAddress;
            ByteBuffer byteBuffer = allocationNativeCreateAllocation.buffer;
            for (int i11 = 0; i11 < i10; i11++) {
                int i12 = i11 * 65536;
                ByteBuffer byteBufferDuplicate = byteBuffer.duplicate();
                byteBufferDuplicate.position(i12);
                byteBufferDuplicate.limit(i12 + 65536);
                arenaPool.offer(new Allocation(byteBufferDuplicate.slice(), 0, arenaBaseAddress + ((long) i12)));
            }
        }
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
    private static native Allocation nativeCreateAllocation(int i10);

    @FastNative
    private static native void nativeFreeAllocation(long j10);
}

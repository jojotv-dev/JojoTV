package jcifs.smb;

import java.util.Arrays;
import jcifs.BufferCache;
import jcifs.Configuration;

/* JADX INFO: loaded from: classes5.dex */
public class BufferCacheImpl implements BufferCache {
    private final int bufferSize;
    private final Object[] cache;
    private int freeBuffers;

    public BufferCacheImpl(Configuration configuration) {
        this(configuration.getBufferCacheSize(), configuration.getMaximumBufferSize());
    }

    @Override // jcifs.BufferCache
    public byte[] getBuffer() {
        synchronized (this.cache) {
            try {
                if (this.freeBuffers > 0) {
                    int i10 = 0;
                    while (true) {
                        Object[] objArr = this.cache;
                        if (i10 >= objArr.length) {
                            break;
                        }
                        Object obj = objArr[i10];
                        if (obj != null) {
                            byte[] bArr = (byte[]) obj;
                            objArr[i10] = null;
                            this.freeBuffers--;
                            return bArr;
                        }
                        i10++;
                    }
                }
                return new byte[this.bufferSize];
            } catch (Throwable th2) {
                throw th2;
            }
        }
    }

    @Override // jcifs.BufferCache
    public void releaseBuffer(byte[] bArr) {
        if (bArr == null) {
            return;
        }
        int i10 = 0;
        Arrays.fill(bArr, (byte) 0);
        synchronized (this.cache) {
            try {
                if (this.freeBuffers < this.cache.length) {
                    while (true) {
                        Object[] objArr = this.cache;
                        if (i10 >= objArr.length) {
                            break;
                        }
                        if (objArr[i10] == null) {
                            objArr[i10] = bArr;
                            this.freeBuffers++;
                            return;
                        }
                        i10++;
                    }
                }
            } catch (Throwable th2) {
                throw th2;
            }
        }
    }

    public BufferCacheImpl(int i10, int i11) {
        this.freeBuffers = 0;
        this.cache = new Object[i10];
        this.bufferSize = i11;
    }
}

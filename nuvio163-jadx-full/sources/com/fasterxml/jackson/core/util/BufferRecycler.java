package com.fasterxml.jackson.core.util;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReferenceArray;

/* JADX INFO: loaded from: classes3.dex */
public class BufferRecycler implements RecyclerPool.WithPool<BufferRecycler> {
    public static final int BYTE_BASE64_CODEC_BUFFER = 3;
    public static final int BYTE_READ_IO_BUFFER = 0;
    public static final int BYTE_WRITE_CONCAT_BUFFER = 2;
    public static final int BYTE_WRITE_ENCODING_BUFFER = 1;
    public static final int CHAR_CONCAT_BUFFER = 1;
    public static final int CHAR_NAME_COPY_BUFFER = 3;
    public static final int CHAR_TEXT_BUFFER = 2;
    public static final int CHAR_TOKEN_BUFFER = 0;
    protected final AtomicReferenceArray<byte[]> _byteBuffers;
    protected final AtomicReferenceArray<char[]> _charBuffers;
    private RecyclerPool<BufferRecycler> _pool;
    private static final int[] BYTE_BUFFER_LENGTHS = {8000, 8000, 2000, 2000};
    private static final int[] CHAR_BUFFER_LENGTHS = {4000, 4000, 200, 200};

    public interface Gettable {
        BufferRecycler bufferRecycler();
    }

    public BufferRecycler() {
        this(4, 4);
    }

    public final byte[] allocByteBuffer(int i10) {
        return allocByteBuffer(i10, 0);
    }

    public final char[] allocCharBuffer(int i10) {
        return allocCharBuffer(i10, 0);
    }

    public byte[] balloc(int i10) {
        return new byte[i10];
    }

    public int byteBufferLength(int i10) {
        return BYTE_BUFFER_LENGTHS[i10];
    }

    public char[] calloc(int i10) {
        return new char[i10];
    }

    public int charBufferLength(int i10) {
        return CHAR_BUFFER_LENGTHS[i10];
    }

    public boolean isLinkedWithPool() {
        return this._pool != null;
    }

    public void releaseByteBuffer(int i10, byte[] bArr) {
        byte[] bArr2 = this._byteBuffers.get(i10);
        if (bArr2 == null || bArr.length > bArr2.length) {
            this._byteBuffers.set(i10, bArr);
        }
    }

    public void releaseCharBuffer(int i10, char[] cArr) {
        char[] cArr2 = this._charBuffers.get(i10);
        if (cArr2 == null || cArr.length > cArr2.length) {
            this._charBuffers.set(i10, cArr);
        }
    }

    @Override // com.fasterxml.jackson.core.util.RecyclerPool.WithPool
    public void releaseToPool() {
        RecyclerPool<BufferRecycler> recyclerPool = this._pool;
        if (recyclerPool != null) {
            this._pool = null;
            recyclerPool.releasePooled(this);
        }
    }

    @Override // com.fasterxml.jackson.core.util.RecyclerPool.WithPool
    public /* bridge */ /* synthetic */ RecyclerPool.WithPool withPool(RecyclerPool recyclerPool) {
        return withPool((RecyclerPool<BufferRecycler>) recyclerPool);
    }

    public BufferRecycler(int i10, int i11) {
        this._byteBuffers = new AtomicReferenceArray<>(i10);
        this._charBuffers = new AtomicReferenceArray<>(i11);
    }

    public byte[] allocByteBuffer(int i10, int i11) {
        int iByteBufferLength = byteBufferLength(i10);
        if (i11 < iByteBufferLength) {
            i11 = iByteBufferLength;
        }
        byte[] andSet = this._byteBuffers.getAndSet(i10, null);
        return (andSet == null || andSet.length < i11) ? balloc(i11) : andSet;
    }

    public char[] allocCharBuffer(int i10, int i11) {
        int iCharBufferLength = charBufferLength(i10);
        if (i11 < iCharBufferLength) {
            i11 = iCharBufferLength;
        }
        char[] andSet = this._charBuffers.getAndSet(i10, null);
        return (andSet == null || andSet.length < i11) ? calloc(i11) : andSet;
    }

    @Override // com.fasterxml.jackson.core.util.RecyclerPool.WithPool
    public BufferRecycler withPool(RecyclerPool<BufferRecycler> recyclerPool) {
        if (this._pool == null) {
            Objects.requireNonNull(recyclerPool);
            this._pool = recyclerPool;
            return this;
        }
        throw new IllegalStateException("BufferRecycler already linked to pool: " + recyclerPool);
    }
}

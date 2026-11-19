package jcifs;

/* JADX INFO: loaded from: classes5.dex */
public interface BufferCache {
    byte[] getBuffer();

    void releaseBuffer(byte[] bArr);
}

package jcifs.util.transport;

/* JADX INFO: loaded from: classes5.dex */
public interface Message {
    byte[] getRawPayload();

    boolean isRetainPayload();

    void retainPayload();

    void setRawPayload(byte[] bArr);
}

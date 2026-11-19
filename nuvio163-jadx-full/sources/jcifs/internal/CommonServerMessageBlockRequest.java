package jcifs.internal;

/* JADX INFO: loaded from: classes5.dex */
public interface CommonServerMessageBlockRequest extends CommonServerMessageBlock, jcifs.util.transport.Request {
    boolean allowChain(CommonServerMessageBlockRequest commonServerMessageBlockRequest);

    CommonServerMessageBlockRequest createCancel();

    @Override // jcifs.util.transport.Request
    CommonServerMessageBlockRequest getNext();

    Integer getOverrideTimeout();

    boolean isResponseAsync();

    void setTid(int i10);

    int size();

    CommonServerMessageBlockRequest split();
}

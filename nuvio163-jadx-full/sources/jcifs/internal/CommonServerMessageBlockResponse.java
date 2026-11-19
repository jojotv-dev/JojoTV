package jcifs.internal;

import jcifs.util.transport.Response;

/* JADX INFO: loaded from: classes5.dex */
public interface CommonServerMessageBlockResponse extends CommonServerMessageBlock, Response {
    @Override // jcifs.util.transport.Response
    CommonServerMessageBlockResponse getNextResponse();

    boolean isAsync();

    void prepare(CommonServerMessageBlockRequest commonServerMessageBlockRequest);
}

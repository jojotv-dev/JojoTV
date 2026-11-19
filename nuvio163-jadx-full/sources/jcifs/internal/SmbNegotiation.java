package jcifs.internal;

/* JADX INFO: loaded from: classes5.dex */
public final class SmbNegotiation {
    private final byte[] negoReqBuffer;
    private final byte[] negoRespBuffer;
    private final SmbNegotiationRequest request;
    private final SmbNegotiationResponse response;

    public SmbNegotiation(SmbNegotiationRequest smbNegotiationRequest, SmbNegotiationResponse smbNegotiationResponse, byte[] bArr, byte[] bArr2) {
        this.request = smbNegotiationRequest;
        this.response = smbNegotiationResponse;
        this.negoReqBuffer = bArr;
        this.negoRespBuffer = bArr2;
    }

    public SmbNegotiationRequest getRequest() {
        return this.request;
    }

    public byte[] getRequestRaw() {
        return this.negoReqBuffer;
    }

    public SmbNegotiationResponse getResponse() {
        return this.response;
    }

    public byte[] getResponseRaw() {
        return this.negoRespBuffer;
    }
}

package jcifs.util.transport;

/* JADX INFO: loaded from: classes5.dex */
public interface Response extends Message {
    void clearReceived();

    void error();

    void exception(Exception exc);

    int getErrorCode();

    Exception getException();

    Long getExpiration();

    int getGrantedCredits();

    long getMid();

    Response getNextResponse();

    boolean isError();

    boolean isReceived();

    boolean isVerifyFailed();

    void received();

    void reset();

    void setExpiration(Long l7);

    void setMid(long j10);

    boolean verifySignature(byte[] bArr, int i10, int i11);
}

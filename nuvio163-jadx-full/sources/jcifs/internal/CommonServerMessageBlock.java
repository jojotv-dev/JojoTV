package jcifs.internal;

import jcifs.util.transport.Message;

/* JADX INFO: loaded from: classes5.dex */
public interface CommonServerMessageBlock extends Message {
    int decode(byte[] bArr, int i10);

    int encode(byte[] bArr, int i10);

    int getCommand();

    SMBSigningDigest getDigest();

    long getMid();

    CommonServerMessageBlockResponse getResponse();

    void reset();

    void setCommand(int i10);

    void setDigest(SMBSigningDigest sMBSigningDigest);

    void setExtendedSecurity(boolean z2);

    void setMid(long j10);

    void setResponse(CommonServerMessageBlockResponse commonServerMessageBlockResponse);

    void setSessionId(long j10);

    void setUid(int i10);
}

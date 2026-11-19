package jcifs.internal;

import jcifs.CIFSContext;
import jcifs.DialectVersion;
import jcifs.util.transport.Response;

/* JADX INFO: loaded from: classes5.dex */
public interface SmbNegotiationResponse extends CommonServerMessageBlock, Response {
    boolean canReuse(CIFSContext cIFSContext, boolean z2);

    int getInitialCredits();

    int getReceiveBufferSize();

    DialectVersion getSelectedDialect();

    int getSendBufferSize();

    int getTransactionBufferSize();

    boolean haveCapabilitiy(int i10);

    boolean isDFSSupported();

    boolean isSigningEnabled();

    boolean isSigningNegotiated();

    boolean isSigningRequired();

    boolean isValid(CIFSContext cIFSContext, SmbNegotiationRequest smbNegotiationRequest);

    void setupRequest(CommonServerMessageBlock commonServerMessageBlock);

    void setupResponse(Response response);
}

package jcifs.util.transport;

import jcifs.CIFSException;

/* JADX INFO: loaded from: classes5.dex */
public class TransportException extends CIFSException {
    private static final long serialVersionUID = 3743631204022885618L;

    public TransportException() {
    }

    @Deprecated
    public Throwable getRootCause() {
        return getCause();
    }

    public TransportException(String str) {
        super(str);
    }

    public TransportException(Throwable th2) {
        super(th2);
    }

    public TransportException(String str, Throwable th2) {
        super(str, th2);
    }
}

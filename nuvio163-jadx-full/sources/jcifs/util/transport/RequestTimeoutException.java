package jcifs.util.transport;

/* JADX INFO: loaded from: classes5.dex */
public class RequestTimeoutException extends TransportException {
    private static final long serialVersionUID = -8825922797594232534L;

    public RequestTimeoutException() {
    }

    public RequestTimeoutException(String str, Throwable th2) {
        super(str, th2);
    }

    public RequestTimeoutException(String str) {
        super(str);
    }

    public RequestTimeoutException(Throwable th2) {
        super(th2);
    }
}

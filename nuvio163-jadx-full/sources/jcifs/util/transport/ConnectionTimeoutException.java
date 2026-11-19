package jcifs.util.transport;

/* JADX INFO: loaded from: classes5.dex */
public class ConnectionTimeoutException extends TransportException {
    private static final long serialVersionUID = 7327198103204592731L;

    public ConnectionTimeoutException() {
    }

    public ConnectionTimeoutException(String str) {
        super(str);
    }

    public ConnectionTimeoutException(Throwable th2) {
        super(th2);
    }

    public ConnectionTimeoutException(String str, Throwable th2) {
        super(str, th2);
    }
}

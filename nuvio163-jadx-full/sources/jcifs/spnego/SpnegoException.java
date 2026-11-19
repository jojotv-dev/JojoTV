package jcifs.spnego;

import jcifs.CIFSException;

/* JADX INFO: loaded from: classes5.dex */
public class SpnegoException extends CIFSException {
    private static final long serialVersionUID = -4591854684249021395L;

    public SpnegoException() {
    }

    public SpnegoException(String str, Throwable th2) {
        super(str, th2);
    }

    public SpnegoException(String str) {
        super(str);
    }

    public SpnegoException(Throwable th2) {
        super(th2);
    }
}

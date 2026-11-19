package jcifs.pac;

import jcifs.CIFSException;

/* JADX INFO: loaded from: classes5.dex */
public class PACDecodingException extends CIFSException {
    private static final long serialVersionUID = 1;

    public PACDecodingException() {
        this(null, null);
    }

    public PACDecodingException(String str) {
        this(str, null);
    }

    public PACDecodingException(Throwable th2) {
        this(null, th2);
    }

    public PACDecodingException(String str, Throwable th2) {
        super(str, th2);
    }
}

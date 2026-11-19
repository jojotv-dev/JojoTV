package jcifs;

import java.io.IOException;

/* JADX INFO: loaded from: classes5.dex */
public class CIFSException extends IOException {
    private static final long serialVersionUID = 7806460518865806784L;

    public CIFSException() {
    }

    public CIFSException(String str, Throwable th2) {
        super(str, th2);
    }

    public CIFSException(String str) {
        super(str);
    }

    public CIFSException(Throwable th2) {
        super(th2);
    }
}

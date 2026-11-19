package jcifs.internal;

import jcifs.CIFSException;

/* JADX INFO: loaded from: classes5.dex */
public class SMBProtocolDecodingException extends CIFSException {
    private static final long serialVersionUID = 4862398838709265475L;

    public SMBProtocolDecodingException() {
    }

    public SMBProtocolDecodingException(String str, Throwable th2) {
        super(str, th2);
    }

    public SMBProtocolDecodingException(String str) {
        super(str);
    }

    public SMBProtocolDecodingException(Throwable th2) {
        super(th2);
    }
}

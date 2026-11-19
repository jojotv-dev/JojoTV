package jcifs.smb;

import jcifs.CIFSException;

/* JADX INFO: loaded from: classes5.dex */
public class SMBProtocolDowngradeException extends CIFSException {
    private static final long serialVersionUID = 1913365058349456689L;

    public SMBProtocolDowngradeException() {
    }

    public SMBProtocolDowngradeException(String str, Throwable th2) {
        super(str, th2);
    }

    public SMBProtocolDowngradeException(String str) {
        super(str);
    }

    public SMBProtocolDowngradeException(Throwable th2) {
        super(th2);
    }
}

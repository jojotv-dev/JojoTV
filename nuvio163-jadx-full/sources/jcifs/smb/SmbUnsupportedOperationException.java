package jcifs.smb;

/* JADX INFO: loaded from: classes5.dex */
public class SmbUnsupportedOperationException extends SmbException {
    private static final long serialVersionUID = -5576981906065713710L;

    public SmbUnsupportedOperationException() {
        super("Operation is not supported with the negotiated capabilities");
    }

    public SmbUnsupportedOperationException(String str) {
        super(str);
    }
}

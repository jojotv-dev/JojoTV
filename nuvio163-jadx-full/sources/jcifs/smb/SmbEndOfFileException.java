package jcifs.smb;

/* JADX INFO: loaded from: classes5.dex */
public class SmbEndOfFileException extends SmbException {
    private static final long serialVersionUID = 298752101881244000L;

    public SmbEndOfFileException() {
        super("Unexpectedly reached end of file");
    }
}

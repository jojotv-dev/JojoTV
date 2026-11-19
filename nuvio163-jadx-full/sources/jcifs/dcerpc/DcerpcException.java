package jcifs.dcerpc;

import jcifs.CIFSException;
import jcifs.smb.WinError;
import jcifs.util.Hexdump;

/* JADX INFO: loaded from: classes5.dex */
public class DcerpcException extends CIFSException implements DcerpcError, WinError {
    private static final long serialVersionUID = -6113895861333916945L;
    private int error;

    public DcerpcException(int i10) {
        super(getMessageByDcerpcError(i10));
        this.error = i10;
    }

    public static String getMessageByDcerpcError(int i10) {
        int length = DcerpcError.DCERPC_FAULT_CODES.length;
        int i11 = 0;
        while (length >= i11) {
            int i12 = (i11 + length) / 2;
            int i13 = DcerpcError.DCERPC_FAULT_CODES[i12];
            if (i10 > i13) {
                i11 = i12 + 1;
            } else {
                if (i10 >= i13) {
                    return DcerpcError.DCERPC_FAULT_MESSAGES[i12];
                }
                length = i12 - 1;
            }
        }
        return "0x" + Hexdump.toHexString(i10, 8);
    }

    public int getErrorCode() {
        return this.error;
    }

    @Deprecated
    public Throwable getRootCause() {
        return getCause();
    }

    public DcerpcException(String str) {
        super(str);
    }

    public DcerpcException(String str, Throwable th2) {
        super(str, th2);
    }
}

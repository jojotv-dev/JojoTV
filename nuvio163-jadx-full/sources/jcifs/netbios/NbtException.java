package jcifs.netbios;

import a0.i;
import jcifs.CIFSException;

/* JADX INFO: loaded from: classes5.dex */
public class NbtException extends CIFSException {
    public static final int ACT_ERR = 6;
    public static final int CALLED_NOT_PRESENT = 130;
    public static final int CFT_ERR = 7;
    public static final int CONNECTION_REFUSED = -1;
    public static final int ERR_NAM_SRVC = 1;
    public static final int ERR_SSN_SRVC = 2;
    public static final int FMT_ERR = 1;
    public static final int IMP_ERR = 4;
    public static final int NOT_LISTENING_CALLED = 128;
    public static final int NOT_LISTENING_CALLING = 129;
    public static final int NO_RESOURCES = 131;
    public static final int RFS_ERR = 5;
    public static final int SRV_ERR = 2;
    public static final int SUCCESS = 0;
    public static final int UNSPECIFIED = 143;
    private static final long serialVersionUID = 492638554095148960L;
    public int errorClass;
    public int errorCode;

    public NbtException(int i10, int i11) {
        super(getErrorString(i10, i11));
        this.errorClass = i10;
        this.errorCode = i11;
    }

    public static String getErrorString(int i10, int i11) {
        if (i10 == 0) {
            return "SUCCESS";
        }
        if (i10 == 1) {
            return i.e(i11, i11 != 1 ? "ERR_NAM_SRVC/" : "ERR_NAM_SRVC/FMT_ERR: Format Error", "Unknown error code: ");
        }
        if (i10 != 2) {
            return i.k("unknown error class: ", i10);
        }
        if (i11 == -1) {
            return "ERR_SSN_SRVC/Connection refused";
        }
        if (i11 == 143) {
            return "ERR_SSN_SRVC/Unspecified error";
        }
        switch (i11) {
            case 128:
                return "ERR_SSN_SRVC/Not listening on called name";
            case 129:
                return "ERR_SSN_SRVC/Not listening for calling name";
            case 130:
                return "ERR_SSN_SRVC/Called name not present";
            case 131:
                return "ERR_SSN_SRVC/Called name present, but insufficient resources";
            default:
                return i.k("ERR_SSN_SRVC/Unknown error code: ", i11);
        }
    }

    @Override // java.lang.Throwable
    public String toString() {
        return new String("errorClass=" + this.errorClass + ",errorCode=" + this.errorCode + ",errorString=" + getErrorString(this.errorClass, this.errorCode));
    }
}

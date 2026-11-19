package jcifs.dcerpc.ndr;

import jcifs.CIFSException;

/* JADX INFO: loaded from: classes5.dex */
public class NdrException extends CIFSException {
    public static final String INVALID_CONFORMANCE = "invalid array conformance";
    public static final String NO_NULL_REF = "ref pointer cannot be null";
    private static final long serialVersionUID = 7621650016319792189L;

    public NdrException(String str) {
        super(str);
    }
}

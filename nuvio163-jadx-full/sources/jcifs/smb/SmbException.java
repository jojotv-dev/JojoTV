package jcifs.smb;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import jcifs.CIFSException;
import jcifs.util.Hexdump;

/* JADX INFO: loaded from: classes5.dex */
public class SmbException extends CIFSException implements NtStatus, DosError, WinError {
    private static final Map<Integer, Integer> dosErrorCodeStatuses;
    private static final Map<Integer, String> errorCodeMessages;
    private static final long serialVersionUID = 484863569441792249L;
    private static final Map<Integer, String> winErrorCodeMessages;
    private int status;

    static {
        HashMap map = new HashMap();
        int i10 = 0;
        int i11 = 0;
        while (true) {
            int[] iArr = NtStatus.NT_STATUS_CODES;
            if (i11 >= iArr.length) {
                break;
            }
            map.put(Integer.valueOf(iArr[i11]), NtStatus.NT_STATUS_MESSAGES[i11]);
            i11++;
        }
        HashMap map2 = new HashMap();
        int i12 = 0;
        while (true) {
            int[][] iArr2 = DosError.DOS_ERROR_CODES;
            if (i12 >= iArr2.length) {
                break;
            }
            map2.put(Integer.valueOf(iArr2[i12][0]), Integer.valueOf(iArr2[i12][1]));
            String str = (String) map.get(Integer.valueOf(iArr2[i12][1]));
            if (str != null) {
                map.put(Integer.valueOf(iArr2[i12][0]), str);
            }
            i12++;
        }
        map.put(0, "NT_STATUS_SUCCESS");
        errorCodeMessages = Collections.unmodifiableMap(map);
        dosErrorCodeStatuses = Collections.unmodifiableMap(map2);
        HashMap map3 = new HashMap();
        while (true) {
            int[] iArr3 = WinError.WINERR_CODES;
            if (i10 >= iArr3.length) {
                winErrorCodeMessages = Collections.unmodifiableMap(map3);
                return;
            } else {
                map3.put(Integer.valueOf(iArr3[i10]), WinError.WINERR_MESSAGES[i10]);
                i10++;
            }
        }
    }

    public SmbException() {
    }

    public static String getMessageByCode(int i10) {
        String str = errorCodeMessages.get(Integer.valueOf(i10));
        if (str != null) {
            return str;
        }
        return "0x" + Hexdump.toHexString(i10, 8);
    }

    public static String getMessageByWinerrCode(int i10) {
        String str = winErrorCodeMessages.get(Integer.valueOf(i10));
        if (str != null) {
            return str;
        }
        return "W" + Hexdump.toHexString(i10, 8);
    }

    public static int getStatusByCode(int i10) {
        if (((-1073741824) & i10) != 0) {
            return i10;
        }
        Map<Integer, Integer> map = dosErrorCodeStatuses;
        return map.containsKey(Integer.valueOf(i10)) ? map.get(Integer.valueOf(i10)).intValue() : NtStatus.NT_STATUS_UNSUCCESSFUL;
    }

    public static SmbException wrap(CIFSException cIFSException) {
        return cIFSException instanceof SmbException ? (SmbException) cIFSException : new SmbException(cIFSException.getMessage(), cIFSException);
    }

    public int getNtStatus() {
        return this.status;
    }

    @Deprecated
    public Throwable getRootCause() {
        return getCause();
    }

    public SmbException(int i10, Throwable th2) {
        super(getMessageByCode(i10), th2);
        this.status = getStatusByCode(i10);
    }

    public SmbException(String str) {
        super(str);
        this.status = NtStatus.NT_STATUS_UNSUCCESSFUL;
    }

    public SmbException(String str, Throwable th2) {
        super(str, th2);
        this.status = NtStatus.NT_STATUS_UNSUCCESSFUL;
    }

    public SmbException(int i10, boolean z2) {
        super(z2 ? getMessageByWinerrCode(i10) : getMessageByCode(i10));
        this.status = z2 ? i10 : getStatusByCode(i10);
    }
}

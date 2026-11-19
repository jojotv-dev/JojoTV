package qj;

import io.ktor.sse.ServerSentEventKt;
import java.security.AccessControlException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

/* JADX INFO: loaded from: classes5.dex */
public class c0 extends u {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final byte[] f50812b;

    public c0(String str) {
        this.f50812b = rn.i.c(str);
        try {
            new SimpleDateFormat("yyMMddHHmmssz", w1.f50915a).parse(t());
        } catch (ParseException e10) {
            throw rn.d.n(e10, "invalid date string");
        }
    }

    /* JADX WARN: Code duplicated, block: B:28:0x004d  */
    public static c0 s(byte[] bArr) {
        int length = bArr.length;
        boolean zI = false;
        if ((length == 11 || length == 13 || length == 15 || length == 17) && c6.a.D(0, 10, bArr) && c6.a.k0(2, bArr) && c6.a.h0(8, bArr) <= 59) {
            if (length != 11) {
                if (length != 13) {
                    if (length == 15) {
                        zI = c6.a.I(10, bArr);
                    } else if (length == 17 && c6.a.i0(10, bArr) && c6.a.h0(10, bArr) <= 59 && c6.a.I(12, bArr)) {
                        zI = true;
                    }
                } else if (c6.a.i0(10, bArr) && c6.a.h0(10, bArr) <= 59 && bArr[12] == 90) {
                    zI = true;
                }
            } else if (bArr[10] == 90) {
                zI = true;
            }
        }
        if (zI) {
            return new c0(bArr);
        }
        throw new IllegalArgumentException("invalid UTCTime format");
    }

    @Override // qj.u, qj.n
    public final int hashCode() {
        return rn.d.l(this.f50812b);
    }

    @Override // qj.u
    public final boolean i(u uVar) {
        if (!(uVar instanceof c0)) {
            return false;
        }
        return Arrays.equals(this.f50812b, ((c0) uVar).f50812b);
    }

    @Override // qj.u
    public final void j(zk.a aVar, boolean z2) {
        aVar.d0(this.f50812b, 23, z2);
    }

    @Override // qj.u
    public final boolean k() {
        return false;
    }

    @Override // qj.u
    public final int m(boolean z2) {
        return zk.a.J(this.f50812b.length, z2);
    }

    @Override // qj.u
    public final u q() {
        boolean zC;
        try {
            String strA = rn.g.a("org.bouncycastle.asn1.allow_non_der_time");
            zC = strA == null ? true : rn.g.c(strA);
        } catch (AccessControlException unused) {
            zC = false;
        }
        if (!zC) {
            byte[] bArr = this.f50812b;
            if (bArr.length == 13 && bArr[12] == 90) {
                for (int i10 = 0; i10 != 12; i10++) {
                    byte b5 = bArr[i10];
                    if (b5 >= 48 && b5 <= 57) {
                    }
                }
            }
            throw new r0("cannot emit UTCTime as DER: not in DER format (see Properties.ASN1_ALLOW_NON_DER_TIME)");
        }
        return this;
    }

    public final String t() {
        StringBuilder sb2;
        String strSubstring;
        String strA = rn.i.a(this.f50812b);
        if (strA.indexOf(45) >= 0 || strA.indexOf(43) >= 0) {
            int iIndexOf = strA.indexOf(45);
            if (iIndexOf < 0) {
                iIndexOf = strA.indexOf(43);
            }
            if (iIndexOf == strA.length() - 3) {
                strA = strA.concat("00");
            }
            if (iIndexOf == 10) {
                sb2 = new StringBuilder();
                sb2.append(strA.substring(0, 10));
                sb2.append("00GMT");
                sb2.append(strA.substring(10, 13));
                sb2.append(ServerSentEventKt.COLON);
                strSubstring = strA.substring(13, 15);
            } else {
                sb2 = new StringBuilder();
                sb2.append(strA.substring(0, 12));
                sb2.append("GMT");
                sb2.append(strA.substring(12, 15));
                sb2.append(ServerSentEventKt.COLON);
                strSubstring = strA.substring(15, 17);
            }
        } else if (strA.length() == 11) {
            sb2 = new StringBuilder();
            sb2.append(strA.substring(0, 10));
            strSubstring = "00GMT+00:00";
        } else {
            sb2 = new StringBuilder();
            sb2.append(strA.substring(0, 12));
            strSubstring = "GMT+00:00";
        }
        sb2.append(strSubstring);
        return sb2.toString();
    }

    public final String toString() {
        return rn.i.a(this.f50812b);
    }

    public c0(byte[] bArr) {
        byte b5;
        byte b10;
        if (bArr.length < 2) {
            throw new IllegalArgumentException("UTCTime string too short");
        }
        this.f50812b = bArr;
        if (bArr.length <= 0 || (b5 = bArr[0]) < 48 || b5 > 57 || bArr.length <= 1 || (b10 = bArr[1]) < 48 || b10 > 57) {
            throw new IllegalArgumentException("illegal characters in UTCTime string");
        }
    }
}

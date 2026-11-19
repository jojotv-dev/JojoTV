package ig;

import com.fasterxml.jackson.core.JsonPointer;
import java.net.Inet4Address;
import java.net.InetAddress;

/* JADX INFO: loaded from: classes4.dex */
public final class f {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final InetAddress f24169a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f24170b;

    public f(InetAddress inetAddress, int i10) {
        this.f24169a = inetAddress;
        this.f24170b = i10;
    }

    public static f a(String str) throws j {
        String strSubstring;
        int i10;
        int iLastIndexOf = str.lastIndexOf(47);
        if (iLastIndexOf >= 0) {
            strSubstring = str.substring(iLastIndexOf + 1);
            try {
                i10 = Integer.parseInt(strSubstring, 10);
                str = str.substring(0, iLastIndexOf);
            } catch (NumberFormatException unused) {
                throw new j(Integer.class, strSubstring, null, null);
            }
        } else {
            strSubstring = "";
            i10 = -1;
        }
        InetAddress inetAddressA = d.a(str);
        int i11 = inetAddressA instanceof Inet4Address ? 32 : 128;
        if (i10 > i11) {
            throw new j(f.class, strSubstring, "Invalid network mask", null);
        }
        if (i10 < 0) {
            i10 = i11;
        }
        return new f(inetAddressA, i10);
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof f)) {
            return false;
        }
        f fVar = (f) obj;
        return this.f24169a.equals(fVar.f24169a) && this.f24170b == fVar.f24170b;
    }

    public final int hashCode() {
        return this.f24169a.hashCode() ^ this.f24170b;
    }

    public final String toString() {
        return this.f24169a.getHostAddress() + JsonPointer.SEPARATOR + this.f24170b;
    }
}

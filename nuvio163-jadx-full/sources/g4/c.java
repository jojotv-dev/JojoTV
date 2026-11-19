package g4;

import android.util.Base64;
import dc.d1;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public final class c {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f18776a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f18777b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final String f18778c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final List f18779d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final String f18780e;

    public c(String str, String str2, String str3, List list) {
        str.getClass();
        this.f18776a = str;
        str2.getClass();
        this.f18777b = str2;
        this.f18778c = str3;
        list.getClass();
        this.f18779d = list;
        this.f18780e = d1.h(str, "-", str2, "-", str3);
    }

    public final String toString() {
        StringBuilder sb2 = new StringBuilder();
        sb2.append("FontRequest {mProviderAuthority: " + this.f18776a + ", mProviderPackage: " + this.f18777b + ", mQuery: " + this.f18778c + ", mCertificates:");
        int i10 = 0;
        while (true) {
            List list = this.f18779d;
            if (i10 >= list.size()) {
                sb2.append("}mCertificatesArray: 0");
                return sb2.toString();
            }
            sb2.append(" [");
            List list2 = (List) list.get(i10);
            for (int i11 = 0; i11 < list2.size(); i11++) {
                sb2.append(" \"");
                sb2.append(Base64.encodeToString((byte[]) list2.get(i11), 0));
                sb2.append("\"");
            }
            sb2.append(" ]");
            i10++;
        }
    }
}

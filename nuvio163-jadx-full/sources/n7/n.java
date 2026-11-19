package n7;

import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public final class n {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f39663a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f39664b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public ArrayList f39665c = null;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public ArrayList f39666d = null;

    public n(int i10, String str) {
        this.f39663a = 0;
        this.f39664b = null;
        this.f39663a = i10 == 0 ? 1 : i10;
        this.f39664b = str;
    }

    public final void a(int i10, String str, String str2) {
        if (this.f39665c == null) {
            this.f39665c = new ArrayList();
        }
        this.f39665c.add(new b(str, i10, str2));
    }

    public final String toString() {
        StringBuilder sb2 = new StringBuilder();
        int i10 = this.f39663a;
        if (i10 == 2) {
            sb2.append("> ");
        } else if (i10 == 3) {
            sb2.append("+ ");
        }
        String str = this.f39664b;
        if (str == null) {
            str = "*";
        }
        sb2.append(str);
        ArrayList<b> arrayList = this.f39665c;
        if (arrayList != null) {
            for (b bVar : arrayList) {
                sb2.append('[');
                String str2 = bVar.f39561a;
                String str3 = bVar.f39563c;
                sb2.append(str2);
                int iB = u4.i.b(bVar.f39562b);
                if (iB == 1) {
                    sb2.append('=');
                    sb2.append(str3);
                } else if (iB == 2) {
                    sb2.append("~=");
                    sb2.append(str3);
                } else if (iB == 3) {
                    sb2.append("|=");
                    sb2.append(str3);
                }
                sb2.append(']');
            }
        }
        ArrayList<e> arrayList2 = this.f39666d;
        if (arrayList2 != null) {
            for (e eVar : arrayList2) {
                sb2.append(':');
                sb2.append(eVar);
            }
        }
        return sb2.toString();
    }
}

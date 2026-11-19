package w9;

import com.fasterxml.jackson.core.JsonPointer;
import java.util.Arrays;

/* JADX INFO: loaded from: classes4.dex */
public final class b {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f62165a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f62166b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final String f62167c;

    public b(String str, String str2, String str3) {
        this.f62166b = str2;
        this.f62165a = str;
        this.f62167c = b(str3);
    }

    public static b a(String str) {
        String[] strArrSplit = b(str).split("\\\\", 3);
        if (strArrSplit.length == 1) {
            return new b(strArrSplit[0], null, null);
        }
        return strArrSplit.length == 2 ? new b(strArrSplit[0], strArrSplit[1], null) : new b(strArrSplit[0], strArrSplit[1], strArrSplit[2]);
    }

    public static String b(String str) {
        if (!bo.a.A(str)) {
            return str;
        }
        String strReplace = str.replace(JsonPointer.SEPARATOR, '\\');
        if (strReplace.charAt(0) == '\\') {
            return (strReplace.length() <= 1 || strReplace.charAt(1) != '\\') ? strReplace.substring(1) : strReplace.substring(2);
        }
        return strReplace;
    }

    public final String c() {
        StringBuilder sb2 = new StringBuilder("\\\\");
        sb2.append(this.f62165a);
        String str = this.f62166b;
        if (str != null && !str.isEmpty()) {
            if (str.charAt(0) != '\\') {
                sb2.append("\\");
            }
            sb2.append(str);
            String str2 = this.f62167c;
            if (bo.a.A(str2)) {
                sb2.append("\\");
                sb2.append(str2);
            }
        }
        return sb2.toString();
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && b.class == obj.getClass()) {
            b bVar = (b) obj;
            if (k9.a.a(this.f62165a, bVar.f62165a) && k9.a.a(this.f62166b, bVar.f62166b) && k9.a.a(this.f62167c, bVar.f62167c)) {
                return true;
            }
        }
        return false;
    }

    public final int hashCode() {
        return Arrays.hashCode(new Object[]{this.f62165a, this.f62166b, this.f62167c});
    }

    public final String toString() {
        return c();
    }

    public b(b bVar, String str) {
        this.f62165a = bVar.f62165a;
        if (bo.a.A(bVar.f62166b)) {
            this.f62166b = bVar.f62166b;
            if (bo.a.A(bVar.f62167c)) {
                this.f62167c = bVar.f62167c + "\\" + b(str);
                return;
            }
            this.f62167c = b(str);
            return;
        }
        throw new IllegalArgumentException("Can only make child SmbPath of fully specified SmbPath");
    }
}

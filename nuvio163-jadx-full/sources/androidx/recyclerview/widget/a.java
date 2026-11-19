package androidx.recyclerview.widget;

/* JADX INFO: loaded from: classes3.dex */
public final class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public int f3872a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public int f3873b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public Object f3874c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public int f3875d;

    public final boolean equals(Object obj) {
        if (this != obj) {
            if (!(obj instanceof a)) {
                return false;
            }
            a aVar = (a) obj;
            int i10 = this.f3872a;
            if (i10 != aVar.f3872a) {
                return false;
            }
            if (i10 != 8 || Math.abs(this.f3875d - this.f3873b) != 1 || this.f3875d != aVar.f3873b || this.f3873b != aVar.f3875d) {
                if (this.f3875d != aVar.f3875d || this.f3873b != aVar.f3873b) {
                    return false;
                }
                Object obj2 = this.f3874c;
                if (obj2 != null) {
                    if (!obj2.equals(aVar.f3874c)) {
                        return false;
                    }
                } else if (aVar.f3874c != null) {
                    return false;
                }
            }
        }
        return true;
    }

    public final int hashCode() {
        return (((this.f3872a * 31) + this.f3873b) * 31) + this.f3875d;
    }

    public final String toString() {
        String str;
        StringBuilder sb2 = new StringBuilder();
        sb2.append(Integer.toHexString(System.identityHashCode(this)));
        sb2.append("[");
        int i10 = this.f3872a;
        if (i10 == 1) {
            str = "add";
        } else if (i10 == 2) {
            str = "rm";
        } else if (i10 != 4) {
            str = i10 != 8 ? "??" : "mv";
        } else {
            str = "up";
        }
        sb2.append(str);
        sb2.append(",s:");
        sb2.append(this.f3873b);
        sb2.append("c:");
        sb2.append(this.f3875d);
        sb2.append(",p:");
        sb2.append(this.f3874c);
        sb2.append("]");
        return sb2.toString();
    }
}

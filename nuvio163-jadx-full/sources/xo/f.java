package xo;

/* JADX INFO: loaded from: classes5.dex */
public final class f {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f64086a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f64087b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final String f64088c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final String f64089d;

    public f(int i10, String str, String str2, String str3) {
        this.f64086a = i10;
        this.f64087b = str;
        this.f64088c = str2;
        this.f64089d = str3;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof f)) {
            return false;
        }
        f fVar = (f) obj;
        return this.f64086a == fVar.f64086a && this.f64087b.equals(fVar.f64087b) && this.f64088c.equals(fVar.f64088c) && this.f64089d.equals(fVar.f64089d);
    }

    public final int hashCode() {
        return (this.f64089d.hashCode() * this.f64088c.hashCode() * this.f64087b.hashCode()) + this.f64086a;
    }

    public final String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(this.f64087b);
        stringBuffer.append('.');
        stringBuffer.append(this.f64088c);
        stringBuffer.append(this.f64089d);
        stringBuffer.append(" (");
        stringBuffer.append(this.f64086a);
        stringBuffer.append(')');
        return stringBuffer.toString();
    }
}

package z9;

/* JADX INFO: loaded from: classes4.dex */
public final class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f65802a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f65803b;

    public a(String str, int i10) {
        this.f65802a = str;
        this.f65803b = i10;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || a.class != obj.getClass()) {
            return false;
        }
        a aVar = (a) obj;
        if (this.f65803b != aVar.f65803b) {
            return false;
        }
        return this.f65802a.equals(aVar.f65802a);
    }

    public final int hashCode() {
        return (this.f65802a.hashCode() * 31) + this.f65803b;
    }
}

package p0;

/* JADX INFO: loaded from: classes.dex */
public final class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f44264a;

    public a(int i10) {
        this.f44264a = i10;
    }

    public final boolean equals(Object obj) {
        if (obj instanceof a) {
            return this.f44264a == ((a) obj).f44264a;
        }
        return false;
    }

    public final int hashCode() {
        return this.f44264a;
    }
}

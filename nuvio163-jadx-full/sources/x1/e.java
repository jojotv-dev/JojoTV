package x1;

/* JADX INFO: loaded from: classes3.dex */
public final class e {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f62990a;

    public final boolean equals(Object obj) {
        if (obj instanceof e) {
            return this.f62990a == ((e) obj).f62990a;
        }
        return false;
    }

    public final int hashCode() {
        return Integer.hashCode(this.f62990a);
    }

    public final String toString() {
        return h1.f.l("AndroidContentDataType(androidAutofillType=", this.f62990a, ')');
    }
}

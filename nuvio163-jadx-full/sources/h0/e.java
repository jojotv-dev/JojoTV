package h0;

/* JADX INFO: loaded from: classes.dex */
public final class e {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final long f20452a;

    public final boolean equals(Object obj) {
        if (obj instanceof e) {
            return this.f20452a == ((e) obj).f20452a;
        }
        return false;
    }

    public final int hashCode() {
        return Long.hashCode(this.f20452a);
    }

    public final String toString() {
        return "GridItemSpan(packedValue=" + this.f20452a + ')';
    }
}

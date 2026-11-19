package od;

/* JADX INFO: loaded from: classes4.dex */
@ji.j
public final class c {
    public static final b Companion = new b();

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final boolean f43718a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final boolean f43719b;

    public /* synthetic */ c(int i10, boolean z2, boolean z10) {
        if ((i10 & 1) == 0) {
            this.f43718a = false;
        } else {
            this.f43718a = z2;
        }
        if ((i10 & 2) == 0) {
            this.f43719b = false;
        } else {
            this.f43719b = z10;
        }
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof c)) {
            return false;
        }
        c cVar = (c) obj;
        return this.f43718a == cVar.f43718a && this.f43719b == cVar.f43719b;
    }

    public final int hashCode() {
        return Boolean.hashCode(this.f43719b) + (Boolean.hashCode(this.f43718a) * 31);
    }

    public final String toString() {
        return "DiscoveryCapabilities(emailPasswordAuth=" + this.f43718a + ", tvLogin=" + this.f43719b + ")";
    }
}

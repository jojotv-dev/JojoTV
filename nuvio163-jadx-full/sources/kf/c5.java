package kf;

/* JADX INFO: loaded from: classes4.dex */
public final class c5 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final long f28559a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f28560b;

    public /* synthetic */ c5() {
        this(System.nanoTime(), 0);
    }

    public static c5 a(c5 c5Var, long j10, int i10, int i11) {
        if ((i11 & 1) != 0) {
            j10 = c5Var.f28559a;
        }
        if ((i11 & 2) != 0) {
            i10 = c5Var.f28560b;
        }
        return new c5(j10, i10);
    }

    public final int b() {
        return this.f28560b;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof c5)) {
            return false;
        }
        c5 c5Var = (c5) obj;
        return this.f28559a == c5Var.f28559a && this.f28560b == c5Var.f28560b;
    }

    public final int hashCode() {
        return Integer.hashCode(this.f28560b) + (Long.hashCode(this.f28559a) * 31);
    }

    public final String toString() {
        return "HomeShuffleRefresh(visit=" + this.f28559a + ", metadata=" + this.f28560b + ")";
    }

    public c5(long j10, int i10) {
        this.f28559a = j10;
        this.f28560b = i10;
    }
}

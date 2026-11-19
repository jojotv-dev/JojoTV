package z9;

/* JADX INFO: loaded from: classes4.dex */
public final class c {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final long f65806a;

    public c(long j10) {
        this.f65806a = j10;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj != null && getClass() == obj.getClass() && this.f65806a == ((c) obj).f65806a;
    }

    public final int hashCode() {
        long j10 = this.f65806a;
        return (int) (j10 ^ (j10 >>> 32));
    }
}

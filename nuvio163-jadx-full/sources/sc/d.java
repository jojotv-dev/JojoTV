package sc;

import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public final class d implements Comparable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f54170b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final long f54171c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final long f54172e;

    public d(long j10, long j11, long j12) {
        this.f54170b = j10;
        this.f54171c = j11;
        this.f54172e = j12;
    }

    @Override // java.lang.Comparable
    public final int compareTo(Object obj) {
        return Long.compare(this.f54170b, ((d) obj).f54170b);
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof d)) {
            return false;
        }
        d dVar = (d) obj;
        return this.f54170b == dVar.f54170b && this.f54171c == dVar.f54171c && this.f54172e == dVar.f54172e;
    }

    public final int hashCode() {
        return Objects.hash(Long.valueOf(this.f54170b), Long.valueOf(this.f54171c), Long.valueOf(this.f54172e));
    }
}

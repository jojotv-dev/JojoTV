package mf;

/* JADX INFO: loaded from: classes4.dex */
public final class hi {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final boolean f36566a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final boolean f36567b;

    public hi(boolean z2, boolean z10) {
        this.f36566a = z2;
        this.f36567b = z10;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof hi)) {
            return false;
        }
        hi hiVar = (hi) obj;
        return this.f36566a == hiVar.f36566a && this.f36567b == hiVar.f36567b;
    }

    public final int hashCode() {
        return Boolean.hashCode(this.f36567b) + (Boolean.hashCode(this.f36566a) * 31);
    }

    public final String toString() {
        return "RatingPreferences(isMdbListActive=" + this.f36566a + ", showStandardRatings=" + this.f36567b + ")";
    }
}

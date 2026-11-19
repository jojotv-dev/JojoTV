package s3;

/* JADX INFO: loaded from: classes.dex */
public final class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final float f54002a;

    public final boolean equals(Object obj) {
        if (obj instanceof a) {
            return Float.compare(this.f54002a, ((a) obj).f54002a) == 0;
        }
        return false;
    }

    public final int hashCode() {
        return Float.hashCode(this.f54002a);
    }

    public final String toString() {
        return "BaselineShift(multiplier=" + this.f54002a + ')';
    }
}

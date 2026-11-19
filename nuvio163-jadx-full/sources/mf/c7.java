package mf;

/* JADX INFO: loaded from: classes4.dex */
public final class c7 extends f8 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final float f35878a;

    public c7(float f10) {
        this.f35878a = f10;
    }

    public final float a() {
        return this.f35878a;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof c7) && Float.compare(this.f35878a, ((c7) obj).f35878a) == 0;
    }

    public final int hashCode() {
        return Float.hashCode(this.f35878a);
    }

    public final String toString() {
        return "OnSetPlaybackSpeed(speed=" + this.f35878a + ")";
    }
}

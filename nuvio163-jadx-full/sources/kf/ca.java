package kf;

/* JADX INFO: loaded from: classes.dex */
public final class ca {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final float f28585a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final float f28586b;

    public ca(float f10, float f11) {
        this.f28585a = f10;
        this.f28586b = f11;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ca)) {
            return false;
        }
        ca caVar = (ca) obj;
        return t3.f.c(this.f28585a, caVar.f28585a) && t3.f.c(this.f28586b, caVar.f28586b);
    }

    public final int hashCode() {
        return Float.hashCode(this.f28586b) + (Float.hashCode(this.f28585a) * 31);
    }

    public final String toString() {
        return a0.i.p("ModernCatalogCardMetrics(width=", t3.f.d(this.f28585a), ", height=", t3.f.d(this.f28586b), ")");
    }
}

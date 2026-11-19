package s3;

/* JADX INFO: loaded from: classes.dex */
public final class f {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final float f54009b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final float f54010c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final float f54011d;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final float f54012a;

    static {
        a(0.0f);
        a(0.5f);
        f54009b = 0.5f;
        a(-1.0f);
        f54010c = -1.0f;
        a(1.0f);
        f54011d = 1.0f;
    }

    public static void a(float f10) {
        if ((0.0f > f10 || f10 > 1.0f) && f10 != -1.0f) {
            n3.a.c("topRatio should be in [0..1] range or -1");
        }
    }

    public static String b(float f10) {
        if (f10 == 0.0f) {
            return "LineHeightStyle.Alignment.Top";
        }
        if (f10 == f54009b) {
            return "LineHeightStyle.Alignment.Center";
        }
        if (f10 == f54010c) {
            return "LineHeightStyle.Alignment.Proportional";
        }
        if (f10 == f54011d) {
            return "LineHeightStyle.Alignment.Bottom";
        }
        return "LineHeightStyle.Alignment(topPercentage = " + f10 + ')';
    }

    public final boolean equals(Object obj) {
        if (obj instanceof f) {
            return Float.compare(this.f54012a, ((f) obj).f54012a) == 0;
        }
        return false;
    }

    public final int hashCode() {
        return Float.hashCode(this.f54012a);
    }

    public final String toString() {
        return b(this.f54012a);
    }
}

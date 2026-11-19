package s3;

import androidx.media3.effect.DebugTraceUtil;

/* JADX INFO: loaded from: classes.dex */
public final class k {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f54023a;

    public /* synthetic */ k(int i10) {
        this.f54023a = i10;
    }

    public static final /* synthetic */ k a(int i10) {
        return new k(i10);
    }

    public static String b(int i10) {
        if (i10 == 1) {
            return "Left";
        }
        if (i10 == 2) {
            return "Right";
        }
        if (i10 == 3) {
            return "Center";
        }
        if (i10 == 4) {
            return "Justify";
        }
        if (i10 == 5) {
            return DebugTraceUtil.EVENT_START;
        }
        if (i10 == 6) {
            return "End";
        }
        return i10 == 0 ? "Unspecified" : "Invalid";
    }

    public final boolean equals(Object obj) {
        if (obj instanceof k) {
            return this.f54023a == ((k) obj).f54023a;
        }
        return false;
    }

    public final int hashCode() {
        return Integer.hashCode(this.f54023a);
    }

    public final String toString() {
        return b(this.f54023a);
    }
}

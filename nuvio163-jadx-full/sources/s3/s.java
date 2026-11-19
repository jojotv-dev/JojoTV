package s3;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class s {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final s f54037c = new s(2, false);

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final s f54038d = new s(1, true);

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f54039a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final boolean f54040b;

    public s(int i10, boolean z2) {
        this.f54039a = i10;
        this.f54040b = z2;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof s)) {
            return false;
        }
        s sVar = (s) obj;
        return this.f54039a == sVar.f54039a && this.f54040b == sVar.f54040b;
    }

    public final int hashCode() {
        return Boolean.hashCode(this.f54040b) + (Integer.hashCode(this.f54039a) * 31);
    }

    public final String toString() {
        if (Intrinsics.areEqual(this, f54037c)) {
            return "TextMotion.Static";
        }
        return Intrinsics.areEqual(this, f54038d) ? "TextMotion.Animated" : "Invalid";
    }
}

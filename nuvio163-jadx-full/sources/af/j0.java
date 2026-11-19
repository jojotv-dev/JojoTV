package af;

import android.graphics.Bitmap;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class j0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Bitmap f1134a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f1135b;

    public j0(Bitmap bitmap, int i10) {
        Intrinsics.checkNotNullParameter(bitmap, "bitmap");
        this.f1134a = bitmap;
        this.f1135b = i10;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof j0)) {
            return false;
        }
        j0 j0Var = (j0) obj;
        return Intrinsics.areEqual(this.f1134a, j0Var.f1134a) && this.f1135b == j0Var.f1135b;
    }

    public final int hashCode() {
        return Integer.hashCode(this.f1135b) + (this.f1134a.hashCode() * 31);
    }

    public final String toString() {
        return "PdfPageResult(bitmap=" + this.f1134a + ", count=" + this.f1135b + ")";
    }
}

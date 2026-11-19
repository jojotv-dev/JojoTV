package u7;

import java.io.Serializable;
import okhttp3.internal.url._UrlKt;

/* JADX INFO: loaded from: classes4.dex */
public final class a implements Serializable {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final a f58283e = new a(new int[0]);

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int[] f58284b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f58285c;

    public a(int[] iArr) {
        int length = iArr.length;
        this.f58284b = iArr;
        this.f58285c = length;
    }

    public static a b(int i10) {
        return new a(new int[]{i10});
    }

    public final int a(int i10) {
        l3.c.v(i10, this.f58285c);
        return this.f58284b[i10];
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof a) {
            a aVar = (a) obj;
            int i10 = aVar.f58285c;
            int i11 = this.f58285c;
            if (i11 == i10) {
                for (int i12 = 0; i12 < i11; i12++) {
                    if (a(i12) == aVar.a(i12)) {
                    }
                }
                return true;
            }
        }
        return false;
    }

    public final int hashCode() {
        int i10 = 1;
        for (int i11 = 0; i11 < this.f58285c; i11++) {
            i10 = (i10 * 31) + this.f58284b[i11];
        }
        return i10;
    }

    public final String toString() {
        int i10 = this.f58285c;
        if (i10 == 0) {
            return _UrlKt.PATH_SEGMENT_ENCODE_SET_URI;
        }
        StringBuilder sb2 = new StringBuilder(i10 * 5);
        sb2.append('[');
        int[] iArr = this.f58284b;
        sb2.append(iArr[0]);
        for (int i11 = 1; i11 < i10; i11++) {
            sb2.append(", ");
            sb2.append(iArr[i11]);
        }
        sb2.append(']');
        return sb2.toString();
    }
}

package bi;

import a0.i;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt__StringsKt;

/* JADX INFO: loaded from: classes3.dex */
public final class a implements Comparable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f6244b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f6245c;

    public a(int i10, int i11) {
        this.f6244b = i10;
        this.f6245c = i11;
        if (i11 < 0) {
            throw new IllegalArgumentException(i.k("Digits must be non-negative, but was ", i11).toString());
        }
    }

    public final int a(int i10) {
        int i11 = this.f6244b;
        int i12 = this.f6245c;
        if (i10 == i12) {
            return i11;
        }
        int[] iArr = b.f6246a;
        return i10 > i12 ? i11 * iArr[i10 - i12] : i11 / iArr[i12 - i10];
    }

    @Override // java.lang.Comparable
    public final int compareTo(Object obj) {
        a other = (a) obj;
        Intrinsics.checkNotNullParameter(other, "other");
        int iMax = Math.max(this.f6245c, other.f6245c);
        return Intrinsics.compare(a(iMax), other.a(iMax));
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof a)) {
            return false;
        }
        a other = (a) obj;
        Intrinsics.checkNotNullParameter(other, "other");
        int iMax = Math.max(this.f6245c, other.f6245c);
        return Intrinsics.compare(a(iMax), other.a(iMax)) == 0;
    }

    public final int hashCode() {
        throw new UnsupportedOperationException("DecimalFraction is not supposed to be used as a hash key");
    }

    public final String toString() {
        StringBuilder sb2 = new StringBuilder();
        int i10 = b.f6246a[this.f6245c];
        int i11 = this.f6244b;
        sb2.append(i11 / i10);
        sb2.append('.');
        sb2.append(StringsKt__StringsKt.removePrefix(String.valueOf((i11 % i10) + i10), (CharSequence) "1"));
        return sb2.toString();
    }
}

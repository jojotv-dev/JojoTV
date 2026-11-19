package g8;

import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public final class b implements Cloneable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public int f18817b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f18818c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public int f18819e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public int[] f18820i;

    public final boolean a(int i10, int i11) {
        return ((this.f18820i[(i10 / 32) + (i11 * this.f18819e)] >>> (i10 & 31)) & 1) != 0;
    }

    public final Object clone() {
        int i10 = this.f18817b;
        int i11 = this.f18818c;
        int i12 = this.f18819e;
        int[] iArr = (int[]) this.f18820i.clone();
        b bVar = new b();
        bVar.f18817b = i10;
        bVar.f18818c = i11;
        bVar.f18819e = i12;
        bVar.f18820i = iArr;
        return bVar;
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof b)) {
            return false;
        }
        b bVar = (b) obj;
        return this.f18817b == bVar.f18817b && this.f18818c == bVar.f18818c && this.f18819e == bVar.f18819e && Arrays.equals(this.f18820i, bVar.f18820i);
    }

    public final int hashCode() {
        int i10 = this.f18817b;
        return Arrays.hashCode(this.f18820i) + (((((((i10 * 31) + i10) * 31) + this.f18818c) * 31) + this.f18819e) * 31);
    }

    public final String toString() {
        int i10 = this.f18818c;
        int i11 = this.f18817b;
        StringBuilder sb2 = new StringBuilder((i11 + 1) * i10);
        for (int i12 = 0; i12 < i10; i12++) {
            for (int i13 = 0; i13 < i11; i13++) {
                sb2.append(a(i13, i12) ? "X " : "  ");
            }
            sb2.append("\n");
        }
        return sb2.toString();
    }
}

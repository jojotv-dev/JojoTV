package g8;

import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public final class a implements Cloneable {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final int[] f18814e = new int[0];

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f18816c = 0;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public int[] f18815b = f18814e;

    public final void a(boolean z2) {
        c(this.f18816c + 1);
        if (z2) {
            int[] iArr = this.f18815b;
            int i10 = this.f18816c;
            int i11 = i10 / 32;
            iArr[i11] = (1 << (i10 & 31)) | iArr[i11];
        }
        this.f18816c++;
    }

    public final void b(int i10, int i11) {
        if (i11 < 0 || i11 > 32) {
            throw new IllegalArgumentException("Num bits must be between 0 and 32");
        }
        int i12 = this.f18816c;
        c(i12 + i11);
        for (int i13 = i11 - 1; i13 >= 0; i13--) {
            if (((1 << i13) & i10) != 0) {
                int[] iArr = this.f18815b;
                int i14 = i12 / 32;
                iArr[i14] = iArr[i14] | (1 << (i12 & 31));
            }
            i12++;
        }
        this.f18816c = i12;
    }

    public final void c(int i10) {
        if (i10 > this.f18815b.length * 32) {
            int[] iArr = new int[(((int) Math.ceil(i10 / 0.75f)) + 31) / 32];
            int[] iArr2 = this.f18815b;
            System.arraycopy(iArr2, 0, iArr, 0, iArr2.length);
            this.f18815b = iArr;
        }
    }

    public final Object clone() {
        int[] iArr = (int[]) this.f18815b.clone();
        int i10 = this.f18816c;
        a aVar = new a();
        aVar.f18815b = iArr;
        aVar.f18816c = i10;
        return aVar;
    }

    public final boolean d(int i10) {
        return ((1 << (i10 & 31)) & this.f18815b[i10 / 32]) != 0;
    }

    public final int e() {
        return (this.f18816c + 7) / 8;
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof a)) {
            return false;
        }
        a aVar = (a) obj;
        return this.f18816c == aVar.f18816c && Arrays.equals(this.f18815b, aVar.f18815b);
    }

    public final int hashCode() {
        return Arrays.hashCode(this.f18815b) + (this.f18816c * 31);
    }

    public final String toString() {
        int i10 = this.f18816c;
        StringBuilder sb2 = new StringBuilder((i10 / 8) + i10 + 1);
        for (int i11 = 0; i11 < this.f18816c; i11++) {
            if ((i11 & 7) == 0) {
                sb2.append(' ');
            }
            sb2.append(d(i11) ? 'X' : '.');
        }
        return sb2.toString();
    }
}

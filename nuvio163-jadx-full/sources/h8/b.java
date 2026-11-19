package h8;

import androidx.media3.exoplayer.rtsp.SessionDescription;

/* JADX INFO: loaded from: classes.dex */
public final class b {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final a f20793a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int[] f20794b;

    public b(a aVar, int[] iArr) {
        if (iArr.length == 0) {
            throw new IllegalArgumentException();
        }
        this.f20793a = aVar;
        int length = iArr.length;
        int i10 = 1;
        if (length <= 1 || iArr[0] != 0) {
            this.f20794b = iArr;
            return;
        }
        while (i10 < length && iArr[i10] == 0) {
            i10++;
        }
        if (i10 == length) {
            this.f20794b = new int[]{0};
            return;
        }
        int i11 = length - i10;
        int[] iArr2 = new int[i11];
        this.f20794b = iArr2;
        System.arraycopy(iArr, i10, iArr2, 0, i11);
    }

    public final b a(b bVar) {
        a aVar = bVar.f20793a;
        a aVar2 = this.f20793a;
        if (!aVar2.equals(aVar)) {
            throw new IllegalArgumentException("GenericGFPolys do not have same GenericGF field");
        }
        if (c()) {
            return bVar;
        }
        if (bVar.c()) {
            return this;
        }
        int[] iArr = bVar.f20794b;
        int[] iArr2 = this.f20794b;
        if (iArr2.length > iArr.length) {
            iArr2 = iArr;
            iArr = iArr2;
        }
        int[] iArr3 = new int[iArr.length];
        int length = iArr.length - iArr2.length;
        System.arraycopy(iArr, 0, iArr3, 0, length);
        for (int i10 = length; i10 < iArr.length; i10++) {
            iArr3[i10] = iArr2[i10 - length] ^ iArr[i10];
        }
        return new b(aVar2, iArr3);
    }

    public final int b() {
        return this.f20794b.length - 1;
    }

    public final boolean c() {
        return this.f20794b[0] == 0;
    }

    public final String toString() {
        if (c()) {
            return SessionDescription.SUPPORTED_SDP_VERSION;
        }
        StringBuilder sb2 = new StringBuilder(b() * 8);
        for (int iB = b(); iB >= 0; iB--) {
            int[] iArr = this.f20794b;
            int i10 = iArr[(iArr.length - 1) - iB];
            if (i10 != 0) {
                if (i10 < 0) {
                    if (iB == b()) {
                        sb2.append("-");
                    } else {
                        sb2.append(" - ");
                    }
                    i10 = -i10;
                } else if (sb2.length() > 0) {
                    sb2.append(" + ");
                }
                if (iB == 0 || i10 != 1) {
                    a aVar = this.f20793a;
                    if (i10 == 0) {
                        aVar.getClass();
                        throw new IllegalArgumentException();
                    }
                    int i11 = aVar.f20788b[i10];
                    if (i11 == 0) {
                        sb2.append('1');
                    } else if (i11 == 1) {
                        sb2.append('a');
                    } else {
                        sb2.append("a^");
                        sb2.append(i11);
                    }
                }
                if (iB != 0) {
                    if (iB == 1) {
                        sb2.append('x');
                    } else {
                        sb2.append("x^");
                        sb2.append(iB);
                    }
                }
            }
        }
        return sb2.toString();
    }
}

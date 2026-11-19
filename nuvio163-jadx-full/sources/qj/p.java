package qj;

import androidx.media3.extractor.ts.TsExtractor;

/* JADX INFO: loaded from: classes5.dex */
public final class p {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final byte[] f50874a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f50875b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f50876c;

    public p(byte[] bArr, int i10) {
        int i11;
        this.f50874a = bArr;
        this.f50875b = i10;
        if (bArr == null) {
            i11 = 0;
        } else {
            int i12 = i10 + 1;
            while (true) {
                i10--;
                if (i10 < 0) {
                    break;
                } else {
                    i12 = (i12 * TsExtractor.TS_STREAM_TYPE_AIT) ^ bArr[i10];
                }
            }
            i11 = i12;
        }
        this.f50876c = i11;
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof p)) {
            return false;
        }
        p pVar = (p) obj;
        byte[] bArr = pVar.f50874a;
        int i10 = pVar.f50875b;
        int i11 = this.f50875b;
        if (i11 == i10) {
            for (int i12 = 0; i12 < i11; i12++) {
                if (this.f50874a[i12] == bArr[i12]) {
                }
            }
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return this.f50876c;
    }
}

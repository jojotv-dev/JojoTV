package io.sentry.android.core.anr;

/* JADX INFO: loaded from: classes5.dex */
public final class b {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final StackTraceElement[] f24606a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f24607b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f24608c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final int f24609d;

    public b(StackTraceElement[] stackTraceElementArr, int i10, int i11) {
        this.f24606a = stackTraceElementArr;
        this.f24607b = i10;
        this.f24608c = i11;
        int iHashCode = 1;
        while (i10 <= this.f24608c) {
            iHashCode = (iHashCode * 31) + this.f24606a[i10].hashCode();
            i10++;
        }
        this.f24609d = iHashCode;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof b)) {
            return false;
        }
        b bVar = (b) obj;
        int i10 = bVar.f24607b;
        if (this.f24609d != bVar.f24609d) {
            return false;
        }
        int i11 = this.f24608c;
        int i12 = this.f24607b;
        int i13 = (i11 - i12) + 1;
        if (i13 != (bVar.f24608c - i10) + 1) {
            return false;
        }
        for (int i14 = 0; i14 < i13; i14++) {
            if (!this.f24606a[i12 + i14].equals(bVar.f24606a[i10 + i14])) {
                return false;
            }
        }
        return true;
    }

    public final int hashCode() {
        return this.f24609d;
    }
}

package androidx.datastore.preferences.protobuf;

/* JADX INFO: loaded from: classes.dex */
public final class z0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final a f2591a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f2592b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final Object[] f2593c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final int f2594d;

    public z0(x xVar, String str, Object[] objArr) {
        this.f2591a = xVar;
        this.f2592b = str;
        this.f2593c = objArr;
        char cCharAt = str.charAt(0);
        if (cCharAt < 55296) {
            this.f2594d = cCharAt;
            return;
        }
        int i10 = cCharAt & 8191;
        int i11 = 13;
        int i12 = 1;
        while (true) {
            int i13 = i12 + 1;
            char cCharAt2 = str.charAt(i12);
            if (cCharAt2 < 55296) {
                this.f2594d = i10 | (cCharAt2 << i11);
                return;
            } else {
                i10 |= (cCharAt2 & 8191) << i11;
                i11 += 13;
                i12 = i13;
            }
        }
    }
}

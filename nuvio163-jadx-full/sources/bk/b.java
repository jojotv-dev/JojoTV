package bk;

/* JADX INFO: loaded from: classes5.dex */
public final class b {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f6273a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final char f6274b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f6275c;

    public b(String str, char c5) {
        if (c5 == '\"' || c5 == '\\') {
            throw new IllegalArgumentException("reserved separator character");
        }
        this.f6273a = str;
        this.f6274b = c5;
        this.f6275c = str.length() < 1 ? 0 : -1;
    }

    public final String a() {
        String str = this.f6273a;
        int length = str.length();
        int i10 = this.f6275c;
        if (i10 >= length) {
            return null;
        }
        int i11 = i10 + 1;
        boolean z2 = false;
        boolean z10 = false;
        while (true) {
            int i12 = this.f6275c + 1;
            this.f6275c = i12;
            if (i12 >= length) {
                if (z2 || z10) {
                    throw new IllegalArgumentException("badly formatted directory string");
                }
                return str.substring(i11, i12);
            }
            char cCharAt = str.charAt(i12);
            if (z2) {
                z2 = false;
            } else if (cCharAt == '\"') {
                z10 = !z10;
            } else if (z10) {
                continue;
            } else if (cCharAt == '\\') {
                z2 = true;
            } else if (cCharAt == this.f6274b) {
                return str.substring(i11, this.f6275c);
            }
        }
    }
}

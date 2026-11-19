package s3;

/* JADX INFO: loaded from: classes.dex */
public final class e {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final int f54007b = 66305;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f54008a;

    public static String a(int i10) {
        String str;
        String str2;
        StringBuilder sb2 = new StringBuilder("LineBreak(strategy=");
        int i11 = i10 & 255;
        String str3 = "Invalid";
        if (i11 == 1) {
            str = "Strategy.Simple";
        } else if (i11 == 2) {
            str = "Strategy.HighQuality";
        } else if (i11 == 3) {
            str = "Strategy.Balanced";
        } else {
            str = i11 == 0 ? "Strategy.Unspecified" : "Invalid";
        }
        sb2.append((Object) str);
        sb2.append(", strictness=");
        int i12 = (i10 >> 8) & 255;
        if (i12 == 1) {
            str2 = "Strictness.None";
        } else if (i12 == 2) {
            str2 = "Strictness.Loose";
        } else if (i12 == 3) {
            str2 = "Strictness.Normal";
        } else if (i12 == 4) {
            str2 = "Strictness.Strict";
        } else {
            str2 = i12 == 0 ? "Strictness.Unspecified" : "Invalid";
        }
        sb2.append((Object) str2);
        sb2.append(", wordBreak=");
        int i13 = (i10 >> 16) & 255;
        if (i13 == 1) {
            str3 = "WordBreak.None";
        } else if (i13 == 2) {
            str3 = "WordBreak.Phrase";
        } else if (i13 == 0) {
            str3 = "WordBreak.Unspecified";
        }
        sb2.append((Object) str3);
        sb2.append(')');
        return sb2.toString();
    }

    public final boolean equals(Object obj) {
        if (obj instanceof e) {
            return this.f54008a == ((e) obj).f54008a;
        }
        return false;
    }

    public final int hashCode() {
        return Integer.hashCode(this.f54008a);
    }

    public final String toString() {
        return a(this.f54008a);
    }
}

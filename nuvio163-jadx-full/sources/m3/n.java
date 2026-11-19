package m3;

/* JADX INFO: loaded from: classes.dex */
public final class n {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f34802a;

    public static String a(int i10) {
        if (i10 == 0) {
            return "Unspecified";
        }
        if (i10 == 1) {
            return "Text";
        }
        if (i10 == 2) {
            return "Ascii";
        }
        if (i10 == 3) {
            return "Number";
        }
        if (i10 == 4) {
            return "Phone";
        }
        if (i10 == 5) {
            return "Uri";
        }
        if (i10 == 6) {
            return "Email";
        }
        if (i10 == 7) {
            return "Password";
        }
        if (i10 == 8) {
            return "NumberPassword";
        }
        return i10 == 9 ? "Decimal" : "Invalid";
    }

    public final boolean equals(Object obj) {
        if (obj instanceof n) {
            return this.f34802a == ((n) obj).f34802a;
        }
        return false;
    }

    public final int hashCode() {
        return Integer.hashCode(this.f34802a);
    }

    public final String toString() {
        return a(this.f34802a);
    }
}

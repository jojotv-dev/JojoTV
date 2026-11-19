package w7;

/* JADX INFO: loaded from: classes.dex */
public final class i {

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final i f62109d = new i("", "", false);

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f62110a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f62111b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final boolean f62112c;

    static {
        new i("\n", "  ", true);
    }

    public i(String str, String str2, boolean z2) {
        if (!str.matches("[\r\n]*")) {
            throw new IllegalArgumentException("Only combinations of \\n and \\r are allowed in newline.");
        }
        if (!str2.matches("[ \t]*")) {
            throw new IllegalArgumentException("Only combinations of spaces and tabs are allowed in indent.");
        }
        this.f62110a = str;
        this.f62111b = str2;
        this.f62112c = z2;
    }
}

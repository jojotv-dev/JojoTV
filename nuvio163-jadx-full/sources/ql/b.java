package ql;

/* JADX INFO: loaded from: classes5.dex */
public final class b implements ek.d {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final b f50932e = new b("falcon-512", 9);

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final b f50933i = new b("falcon-1024", 10);

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f50934b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f50935c;

    public b(String str, int i10) {
        if (i10 < 1 || i10 > 10) {
            throw new IllegalArgumentException("Log N degree should be between 1 and 10");
        }
        this.f50934b = str;
        this.f50935c = i10;
    }
}

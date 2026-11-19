package v1;

/* JADX INFO: loaded from: classes.dex */
public final class p extends Exception {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f60638b;

    public p(String str) {
        super(str);
        this.f60638b = str;
    }

    @Override // java.lang.Throwable
    public final String getMessage() {
        return this.f60638b;
    }
}

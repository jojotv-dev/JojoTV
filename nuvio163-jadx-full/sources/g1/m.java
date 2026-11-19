package g1;

/* JADX INFO: loaded from: classes.dex */
public final class m extends IllegalStateException {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f18499b;

    public m(String str) {
        this.f18499b = str;
    }

    @Override // java.lang.Throwable
    public final String getMessage() {
        return this.f18499b;
    }
}

package ao;

/* JADX INFO: loaded from: classes5.dex */
public final class p extends zn.n {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f4178b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final n f4179c;

    public p(String str, int i10, int i11, int i12) {
        super(str, null);
        this.f4179c = new n(str, i10, i11);
        this.f4178b = i12;
    }

    @Override // java.lang.Throwable
    public final String getMessage() {
        return this.f4179c.getMessage() + ", QuicDetailedErrorCode=" + this.f4178b;
    }
}

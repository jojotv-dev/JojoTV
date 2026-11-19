package o6;

/* JADX INFO: loaded from: classes.dex */
public final class h implements AutoCloseable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final c f43525b;

    public h(c cVar) {
        this.f43525b = cVar;
    }

    @Override // java.lang.AutoCloseable
    public final void close() {
        this.f43525b.close();
    }
}

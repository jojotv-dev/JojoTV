package u1;

/* JADX INFO: loaded from: classes.dex */
public abstract class f0 implements e0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final q1.a f57983b = new q1.a(0);

    public final boolean f(int i10) {
        return (i10 & this.f57983b.get()) != 0;
    }

    public final void g(int i10) {
        q1.a aVar;
        int i11;
        do {
            aVar = this.f57983b;
            i11 = aVar.get();
            if ((i11 & i10) != 0) {
                return;
            }
        } while (!aVar.compareAndSet(i11, i11 | i10));
    }
}

package androidx.datastore.preferences.protobuf;

/* JADX INFO: loaded from: classes.dex */
public final class i0 implements p0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public p0[] f2487a;

    @Override // androidx.datastore.preferences.protobuf.p0
    public final z0 a(Class cls) {
        for (p0 p0Var : this.f2487a) {
            if (p0Var.b(cls)) {
                return p0Var.a(cls);
            }
        }
        throw new UnsupportedOperationException("No factory is available for message type: ".concat(cls.getName()));
    }

    @Override // androidx.datastore.preferences.protobuf.p0
    public final boolean b(Class cls) {
        for (p0 p0Var : this.f2487a) {
            if (p0Var.b(cls)) {
                return true;
            }
        }
        return false;
    }
}

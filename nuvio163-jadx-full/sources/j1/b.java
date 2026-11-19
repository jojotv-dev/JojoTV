package j1;

/* JADX INFO: loaded from: classes.dex */
public final class b {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public int f26933a;

    public b(int i10) {
        this.f26933a = i10;
    }

    public final boolean a() {
        return this.f26933a != Integer.MIN_VALUE;
    }

    public final String toString() {
        StringBuilder sb2 = new StringBuilder();
        sb2.append(super.toString());
        sb2.append("{ location = ");
        return h1.f.j(this.f26933a, " }", sb2);
    }
}

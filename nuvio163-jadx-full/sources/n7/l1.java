package n7;

/* JADX INFO: loaded from: classes.dex */
public abstract class l1 extends v0 {
    @Override // n7.v0, n7.w0
    public final void j(a1 a1Var) throws a2 {
        if (a1Var instanceof k1) {
            this.f39756i.add(a1Var);
            return;
        }
        throw new a2("Text content elements cannot contain " + a1Var + " elements.");
    }
}

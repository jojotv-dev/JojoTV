package w;

/* JADX INFO: loaded from: classes.dex */
public interface d2 extends e2 {
    @Override // w.b2
    default long b(p pVar, p pVar2, p pVar3) {
        return ((long) (l() + k())) * 1000000;
    }

    int k();

    int l();
}

package w;

/* JADX INFO: loaded from: classes.dex */
public interface b2 {
    boolean a();

    long b(p pVar, p pVar2, p pVar3);

    p e(long j10, p pVar, p pVar2, p pVar3);

    p g(long j10, p pVar, p pVar2, p pVar3);

    default p n(p pVar, p pVar2, p pVar3) {
        return g(b(pVar, pVar2, pVar3), pVar, pVar2, pVar3);
    }
}

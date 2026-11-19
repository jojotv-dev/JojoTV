package ai;

/* JADX INFO: loaded from: classes3.dex */
public interface c1 {
    void B(Integer num);

    Integer C();

    Integer e();

    Integer f();

    void i(g gVar);

    default void j(bi.a aVar) {
        r(aVar != null ? Integer.valueOf(aVar.a(9)) : null);
    }

    Integer l();

    void m(Integer num);

    g o();

    void q(Integer num);

    void r(Integer num);

    Integer t();

    void u(Integer num);

    default bi.a v() {
        Integer numE = e();
        if (numE != null) {
            return new bi.a(numE.intValue(), 9);
        }
        return null;
    }
}

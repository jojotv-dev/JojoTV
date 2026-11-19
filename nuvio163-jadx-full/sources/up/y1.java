package up;

/* JADX INFO: loaded from: classes5.dex */
public abstract class y1 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final b1 f60315a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final b1 f60316b;

    static {
        b1 b1Var = new b1("DNS Rcode", 2);
        f60315a = b1Var;
        b1 b1Var2 = new b1("TSIG rcode", 2);
        f60316b = b1Var2;
        b1Var.f60072f = 4095;
        b1Var.e("RESERVED");
        b1Var.a(0, "NOERROR");
        b1Var.a(1, "FORMERR");
        b1Var.a(2, "SERVFAIL");
        b1Var.a(3, "NXDOMAIN");
        b1Var.a(4, "NOTIMP");
        b1Var.b(4);
        Integer numF = b1.f(4);
        b1Var.f60067a.put(b1Var.d("NOTIMPL"), numF);
        b1Var.a(5, "REFUSED");
        b1Var.a(6, "YXDOMAIN");
        b1Var.a(7, "YXRRSET");
        b1Var.a(8, "NXRRSET");
        b1Var.a(9, "NOTAUTH");
        b1Var.a(10, "NOTZONE");
        b1Var.a(16, "BADVERS");
        b1Var2.f60072f = 65535;
        b1Var2.e("RESERVED");
        b1Var2.f60067a.putAll(b1Var.f60067a);
        b1Var2.f60068b.putAll(b1Var.f60068b);
        b1Var2.a(16, "BADSIG");
        b1Var2.a(17, "BADKEY");
        b1Var2.a(18, "BADTIME");
        b1Var2.a(19, "BADMODE");
    }
}

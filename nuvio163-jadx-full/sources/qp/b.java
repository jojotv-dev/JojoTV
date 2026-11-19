package qp;

import java.io.IOException;

/* JADX INFO: loaded from: classes3.dex */
public interface b {
    void A(String str);

    default boolean B(int i10) {
        int iA = q5.c.a(i10);
        if (iA == 0) {
            return f();
        }
        if (iA == 10) {
            return b();
        }
        if (iA == 20) {
            return e();
        }
        if (iA == 30) {
            return a();
        }
        if (iA == 40) {
            return p();
        }
        throw new IllegalArgumentException("Level [" + q5.c.u(i10) + "] not recognized.");
    }

    void C(Object obj, String str);

    void D(Exception exc, String str);

    void E(String str, IOException iOException);

    boolean a();

    boolean b();

    void c(String str);

    void d(Long l7, IOException iOException);

    boolean e();

    boolean f();

    void g(String str, Object... objArr);

    String getName();

    void h(Object obj, String str, Object obj2);

    void i(Object obj, String str);

    void j(String str, Object... objArr);

    void k(Object obj, String str, Object obj2);

    void l(Object... objArr);

    void m(Object obj, String str);

    void n(String str, Throwable th2);

    void o(Object obj, String str);

    boolean p();

    void q(Object obj, String str);

    void r(String str);

    void s(Object obj, String str, Object obj2);

    void t(Object... objArr);

    void u(Object... objArr);

    void v(Object obj, String str, Object obj2);

    void w(String str, Throwable th2);

    void x(String str);

    void y(String str);

    void z(Exception exc, String str);
}

package g1;

import kotlin.jvm.functions.Function2;

/* JADX INFO: loaded from: classes.dex */
public interface a {
    void c(int i10, int i11, int i12);

    void d(int i10, int i11);

    default void f(Object obj, Function2 function2) {
        function2.invoke(j(), obj);
    }

    void h(int i10, Object obj);

    Object j();

    void m(int i10, Object obj);

    void o(Object obj);

    void p();

    void r();

    default void i() {
    }
}

package w1;

import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

/* JADX INFO: loaded from: classes.dex */
public interface q {
    Object a(Object obj, Function2 function2);

    boolean c(Function1 function1);

    default q d(q qVar) {
        return qVar == n.f61608b ? this : new k(this, qVar);
    }
}

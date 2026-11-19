package w1;

import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

/* JADX INFO: loaded from: classes.dex */
public interface o extends q {
    @Override // w1.q
    default Object a(Object obj, Function2 function2) {
        return function2.invoke(obj, this);
    }

    @Override // w1.q
    default boolean c(Function1 function1) {
        return ((Boolean) function1.invoke(this)).booleanValue();
    }
}

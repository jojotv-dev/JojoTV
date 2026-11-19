package u2;

import java.util.Map;
import kotlin.jvm.functions.Function1;

/* JADX INFO: loaded from: classes.dex */
public interface w0 extends v {
    default v0 X(int i10, int i11, Map map, Function1 function1) {
        return z0(i10, i11, map, null, function1);
    }

    v0 z0(int i10, int i11, Map map, Function1 function1, Function1 function2);
}

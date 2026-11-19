package rd;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.AdaptedFunctionReference;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.Request;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class k extends AdaptedFunctionReference implements Function2 {
    @Override // kotlin.jvm.functions.Function2
    public final Object invoke(Object obj, Object obj2) {
        String p10 = (String) obj;
        String p11 = (String) obj2;
        Intrinsics.checkNotNullParameter(p10, "p0");
        Intrinsics.checkNotNullParameter(p11, "p1");
        ((Request.Builder) this.receiver).header(p10, p11);
        return Unit.INSTANCE;
    }
}

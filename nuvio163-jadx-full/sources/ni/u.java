package ni;

import java.lang.ref.SoftReference;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes3.dex */
public final class u extends ClassValue {
    @Override // java.lang.ClassValue
    public final Object computeValue(Class type) {
        Intrinsics.checkNotNullParameter(type, "type");
        d1 d1Var = new d1();
        d1Var.f42764a = new SoftReference(null);
        return d1Var;
    }
}

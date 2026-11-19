package ni;

import java.lang.ref.SoftReference;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes3.dex */
public final class d1 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public volatile SoftReference f42764a;

    public final synchronized Object a(Function0 factory) {
        Intrinsics.checkNotNullParameter(factory, "factory");
        Object obj = this.f42764a.get();
        if (obj != null) {
            return obj;
        }
        Object objInvoke = factory.invoke();
        this.f42764a = new SoftReference(objInvoke);
        return objInvoke;
    }
}

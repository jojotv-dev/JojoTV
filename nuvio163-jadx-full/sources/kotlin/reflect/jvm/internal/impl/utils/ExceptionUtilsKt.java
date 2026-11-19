package kotlin.reflect.jvm.internal.impl.utils;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes5.dex */
public final class ExceptionUtilsKt {
    public static final boolean isProcessCanceledException(Throwable th2) {
        Intrinsics.checkNotNullParameter(th2, "<this>");
        Class<?> superclass = th2.getClass();
        while (!Intrinsics.areEqual(superclass.getCanonicalName(), "com.intellij.openapi.progress.ProcessCanceledException")) {
            superclass = superclass.getSuperclass();
            if (superclass == null) {
                return false;
            }
        }
        return true;
    }

    public static final RuntimeException rethrow(Throwable e10) throws Throwable {
        Intrinsics.checkNotNullParameter(e10, "e");
        throw e10;
    }
}

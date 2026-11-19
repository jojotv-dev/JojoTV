package w;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public interface r1 {
    Object a();

    default boolean b(Enum r9, Enum r10) {
        return Intrinsics.areEqual(r9, c()) && Intrinsics.areEqual(r10, a());
    }

    Object c();
}

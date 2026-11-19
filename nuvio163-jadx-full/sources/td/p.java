package td;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class p extends Exception {
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public p(String message) {
        super(message);
        Intrinsics.checkNotNullParameter(message, "message");
    }
}

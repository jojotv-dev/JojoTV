package fe;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class i4 extends Exception {
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public i4(String message) {
        super(message);
        Intrinsics.checkNotNullParameter(message, "message");
    }
}

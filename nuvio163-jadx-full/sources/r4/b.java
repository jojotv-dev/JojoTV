package r4;

import java.io.IOException;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class b extends IOException {
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public b(String message, int i10) {
        super(message, null);
        Intrinsics.checkNotNullParameter(message, "message");
    }
}

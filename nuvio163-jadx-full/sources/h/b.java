package h;

import android.content.Context;
import android.content.Intent;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public abstract class b {
    public abstract Intent createIntent(Context context, Object obj);

    public a getSynchronousResult(Context context, Object obj) {
        Intrinsics.checkNotNullParameter(context, "context");
        return null;
    }

    public abstract Object parseResult(int i10, Intent intent);
}

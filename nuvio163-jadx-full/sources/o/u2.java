package o;

import android.content.Context;
import android.content.ContextWrapper;

/* JADX INFO: loaded from: classes.dex */
public abstract class u2 extends ContextWrapper {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final Object f43336a = null;

    public static void a(Context context) {
        if (context.getResources() instanceof w2) {
            return;
        }
        context.getResources();
        int i10 = i3.f43202a;
    }
}

package androidx.core.app;

import android.content.res.Configuration;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes3.dex */
public final class l {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final boolean f2298a;

    public l(boolean z2) {
        this.f2298a = z2;
    }

    public final boolean a() {
        return this.f2298a;
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public l(boolean z2, Configuration newConfig) {
        this(z2);
        Intrinsics.checkNotNullParameter(newConfig, "newConfig");
    }
}

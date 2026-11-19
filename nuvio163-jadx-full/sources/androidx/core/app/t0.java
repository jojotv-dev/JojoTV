package androidx.core.app;

import android.content.res.Configuration;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes3.dex */
public final class t0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final boolean f2365a;

    public t0(boolean z2) {
        this.f2365a = z2;
    }

    public final boolean a() {
        return this.f2365a;
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public t0(boolean z2, Configuration newConfig) {
        this(z2);
        Intrinsics.checkNotNullParameter(newConfig, "newConfig");
    }
}

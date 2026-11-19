package ce;

import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Object f6773a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Function0 f6774b;

    public a(Object latch, Function0 release) {
        Intrinsics.checkNotNullParameter(latch, "latch");
        Intrinsics.checkNotNullParameter(release, "release");
        this.f6773a = latch;
        this.f6774b = release;
    }
}

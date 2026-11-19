package re;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class n {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final HashMap f53145a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Map f53146b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final Set f53147c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final boolean f53148d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final int f53149e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public final boolean f53150f;

    public n(HashMap schedule, Map logos, Set truncated, boolean z2, int i10, boolean z10) {
        Intrinsics.checkNotNullParameter(schedule, "schedule");
        Intrinsics.checkNotNullParameter(logos, "logos");
        Intrinsics.checkNotNullParameter(truncated, "truncated");
        this.f53145a = schedule;
        this.f53146b = logos;
        this.f53147c = truncated;
        this.f53148d = z2;
        this.f53149e = i10;
        this.f53150f = z10;
    }

    public final n a() {
        return new n(this.f53145a, this.f53146b, this.f53147c, this.f53148d, this.f53149e, true);
    }

    public final boolean b() {
        return this.f53148d && this.f53149e > 0 && !this.f53150f;
    }
}

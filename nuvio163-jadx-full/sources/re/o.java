package re;

import java.util.HashMap;
import java.util.Map;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class o {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final HashMap f53170a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Map f53171b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final long f53172c;

    public o(HashMap schedule, HashMap logos, long j10) {
        Intrinsics.checkNotNullParameter(schedule, "schedule");
        Intrinsics.checkNotNullParameter(logos, "logos");
        this.f53170a = schedule;
        this.f53171b = logos;
        this.f53172c = j10;
    }
}

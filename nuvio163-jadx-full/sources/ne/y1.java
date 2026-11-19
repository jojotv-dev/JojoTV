package ne;

import java.util.HashMap;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class y1 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final List f41892a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final List f41893b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final HashMap f41894c;

    public y1(List day, List week, HashMap leagues) {
        Intrinsics.checkNotNullParameter(day, "day");
        Intrinsics.checkNotNullParameter(week, "week");
        Intrinsics.checkNotNullParameter(leagues, "leagues");
        this.f41892a = day;
        this.f41893b = week;
        this.f41894c = leagues;
    }
}

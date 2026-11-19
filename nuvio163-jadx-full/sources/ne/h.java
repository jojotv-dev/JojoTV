package ne;

import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class h {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final List f40532a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final List f40533b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final List f40534c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final List f40535d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final int f40536e;

    public h(List weekLong, List live, List upcoming, List finished, int i10) {
        Intrinsics.checkNotNullParameter(weekLong, "weekLong");
        Intrinsics.checkNotNullParameter(live, "live");
        Intrinsics.checkNotNullParameter(upcoming, "upcoming");
        Intrinsics.checkNotNullParameter(finished, "finished");
        this.f40532a = weekLong;
        this.f40533b = live;
        this.f40534c = upcoming;
        this.f40535d = finished;
        this.f40536e = i10;
    }

    public final List a() {
        return this.f40535d;
    }

    public final List b() {
        return this.f40533b;
    }

    public final List c() {
        return this.f40534c;
    }

    public final List d() {
        return this.f40532a;
    }
}

package ne;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class u3 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final long f41641a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final ArrayList f41642b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final HashMap f41643c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final HashSet f41644d;

    public u3(long j10, ArrayList names, HashMap tokenIndex, HashSet discNames) {
        Intrinsics.checkNotNullParameter(names, "names");
        Intrinsics.checkNotNullParameter(tokenIndex, "tokenIndex");
        Intrinsics.checkNotNullParameter(discNames, "discNames");
        this.f41641a = j10;
        this.f41642b = names;
        this.f41643c = tokenIndex;
        this.f41644d = discNames;
    }

    public final Set a() {
        return this.f41644d;
    }

    public final long b() {
        return this.f41641a;
    }

    public final List c() {
        return this.f41642b;
    }

    public final Map d() {
        return this.f41643c;
    }
}

package md;

import java.util.ArrayList;
import java.util.Set;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class g2 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f35129a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f35130b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final ArrayList f35131c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final boolean f35132d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final Set f35133e;

    public g2(int i10, long j10, ArrayList servers, boolean z2, Set set) {
        Intrinsics.checkNotNullParameter(servers, "servers");
        this.f35129a = i10;
        this.f35130b = j10;
        this.f35131c = servers;
        this.f35132d = z2;
        this.f35133e = set;
    }
}

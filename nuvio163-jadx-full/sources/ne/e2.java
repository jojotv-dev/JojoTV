package ne;

import java.util.HashMap;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class e2 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f40271a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final HashMap f40272b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final List f40273c;

    public e2(String etag, HashMap stamps, List days) {
        Intrinsics.checkNotNullParameter(etag, "etag");
        Intrinsics.checkNotNullParameter(stamps, "stamps");
        Intrinsics.checkNotNullParameter(days, "days");
        this.f40271a = etag;
        this.f40272b = stamps;
        this.f40273c = days;
    }
}

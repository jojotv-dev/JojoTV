package ne;

import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class x1 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f41840a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final List f41841b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final String f41842c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final List f41843d;

    public x1(String str, String str2, List dayStamps, List weekStamps) {
        Intrinsics.checkNotNullParameter(dayStamps, "dayStamps");
        Intrinsics.checkNotNullParameter(weekStamps, "weekStamps");
        this.f41840a = str;
        this.f41841b = dayStamps;
        this.f41842c = str2;
        this.f41843d = weekStamps;
    }
}

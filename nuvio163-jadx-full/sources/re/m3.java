package re;

import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class m3 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final List f53143a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final List f53144b;

    public m3(List channels, List groupOrder) {
        Intrinsics.checkNotNullParameter(channels, "channels");
        Intrinsics.checkNotNullParameter(groupOrder, "groupOrder");
        this.f53143a = channels;
        this.f53144b = groupOrder;
    }
}

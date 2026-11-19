package re;

import java.util.ArrayList;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class f0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final ArrayList f52927a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final List f52928b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final ArrayList f52929c;

    public f0(ArrayList channels, List epgUrls, ArrayList groupOrder) {
        Intrinsics.checkNotNullParameter(channels, "channels");
        Intrinsics.checkNotNullParameter(epgUrls, "epgUrls");
        Intrinsics.checkNotNullParameter(groupOrder, "groupOrder");
        this.f52927a = channels;
        this.f52928b = epgUrls;
        this.f52929c = groupOrder;
    }
}

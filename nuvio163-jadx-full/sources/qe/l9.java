package qe;

import java.util.ArrayList;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class l9 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final ArrayList f49362a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final List f49363b;

    public l9(ArrayList visible, List gaps) {
        Intrinsics.checkNotNullParameter(visible, "visible");
        Intrinsics.checkNotNullParameter(gaps, "gaps");
        this.f49362a = visible;
        this.f49363b = gaps;
    }
}

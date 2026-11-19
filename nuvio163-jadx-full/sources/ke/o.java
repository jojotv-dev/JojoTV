package ke;

import androidx.media3.common.Format;
import java.util.List;
import java.util.Set;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.flow.FlowKt;
import kotlinx.coroutines.flow.MutableStateFlow;
import kotlinx.coroutines.flow.StateFlow;
import kotlinx.coroutines.flow.StateFlowKt;

/* JADX INFO: loaded from: classes4.dex */
public final class o {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final List f28218a = CollectionsKt.listOf((Object[]) new String[]{"cc1", "708", "cc2", "cc3", "cc4"});

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final List f28219b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final MutableStateFlow f28220c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final StateFlow f28221d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static volatile int f28222e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public static volatile long f28223f;

    static {
        Format formatB = b(1);
        Format formatB2 = b(2);
        Format formatB3 = b(3);
        Format formatB4 = b(4);
        Format formatBuild = new Format.Builder().setId("708").setSampleMimeType("application/cea-708").setAccessibilityChannel(1).build();
        Intrinsics.checkNotNullExpressionValue(formatBuild, "build(...)");
        f28219b = CollectionsKt.listOf((Object[]) new Format[]{formatB, formatB2, formatB3, formatB4, formatBuild});
        MutableStateFlow MutableStateFlow = StateFlowKt.MutableStateFlow(SetsKt.emptySet());
        f28220c = MutableStateFlow;
        f28221d = FlowKt.asStateFlow(MutableStateFlow);
    }

    public static final void a(int i10, String str) {
        Object value;
        if (i10 == f28222e) {
            MutableStateFlow mutableStateFlow = f28220c;
            if (((Set) mutableStateFlow.getValue()).contains(str)) {
                return;
            }
            do {
                value = mutableStateFlow.getValue();
            } while (!mutableStateFlow.compareAndSet(value, SetsKt.plus((Set<? extends String>) value, str)));
        }
    }

    public static Format b(int i10) {
        Format formatBuild = new Format.Builder().setId("cc" + i10).setSampleMimeType("application/cea-608").setAccessibilityChannel(i10).build();
        Intrinsics.checkNotNullExpressionValue(formatBuild, "build(...)");
        return formatBuild;
    }

    public static String c(Format f10) {
        Intrinsics.checkNotNullParameter(f10, "f");
        String str = f10.sampleMimeType;
        if (str == null) {
            return null;
        }
        int iHashCode = str.hashCode();
        if (iHashCode != 1566015601) {
            if (iHashCode == 1566016562 && str.equals("application/cea-708")) {
                return "708";
            }
            return null;
        }
        if (!str.equals("application/cea-608")) {
            return null;
        }
        int i10 = f10.accessibilityChannel;
        if (i10 == 2) {
            return "cc2";
        }
        if (i10 != 3) {
            return i10 != 4 ? "cc1" : "cc4";
        }
        return "cc3";
    }

    public static boolean d() {
        return ((Set) f28220c.getValue()).isEmpty() && f28223f > 0 && System.currentTimeMillis() - f28223f > 20000;
    }
}

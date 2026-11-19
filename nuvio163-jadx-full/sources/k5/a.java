package k5;

import android.os.Handler;
import android.view.FrameMetrics;
import android.view.Window;
import java.util.Iterator;
import java.util.List;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class a implements Window.OnFrameMetricsAvailableListener {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static Handler f27871b;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final List f27872a;

    public a(List delegates) {
        Intrinsics.checkNotNullParameter(delegates, "delegates");
        this.f27872a = delegates;
    }

    @Override // android.view.Window.OnFrameMetricsAvailableListener
    public final void onFrameMetricsAvailable(Window window, FrameMetrics frameMetrics, int i10) {
        synchronized (this) {
            try {
                Iterator it = this.f27872a.iterator();
                while (it.hasNext()) {
                    ((Window.OnFrameMetricsAvailableListener) it.next()).onFrameMetricsAvailable(window, frameMetrics, i10);
                }
                Unit unit = Unit.INSTANCE;
            } catch (Throwable th2) {
                throw th2;
            }
        }
    }
}

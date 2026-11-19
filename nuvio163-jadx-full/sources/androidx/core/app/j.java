package androidx.core.app;

import android.util.SparseIntArray;
import android.view.FrameMetrics;
import android.view.Window;

/* JADX INFO: loaded from: classes3.dex */
public final class j implements Window.OnFrameMetricsAvailableListener {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ k f2284a;

    public j(k kVar) {
        this.f2284a = kVar;
    }

    @Override // android.view.Window.OnFrameMetricsAvailableListener
    public final void onFrameMetricsAvailable(Window window, FrameMetrics frameMetrics, int i10) {
        k kVar = this.f2284a;
        int i11 = kVar.f2289b;
        if ((i11 & 1) != 0) {
            k.a(((SparseIntArray[]) kVar.f2290c)[0], frameMetrics.getMetric(8));
        }
        if ((i11 & 2) != 0) {
            k.a(((SparseIntArray[]) kVar.f2290c)[1], frameMetrics.getMetric(1));
        }
        if ((i11 & 4) != 0) {
            k.a(((SparseIntArray[]) kVar.f2290c)[2], frameMetrics.getMetric(3));
        }
        if ((i11 & 8) != 0) {
            k.a(((SparseIntArray[]) kVar.f2290c)[3], frameMetrics.getMetric(4));
        }
        if ((i11 & 16) != 0) {
            k.a(((SparseIntArray[]) kVar.f2290c)[4], frameMetrics.getMetric(5));
        }
        if ((i11 & 64) != 0) {
            k.a(((SparseIntArray[]) kVar.f2290c)[6], frameMetrics.getMetric(7));
        }
        if ((i11 & 32) != 0) {
            k.a(((SparseIntArray[]) kVar.f2290c)[5], frameMetrics.getMetric(6));
        }
        if ((i11 & 128) != 0) {
            k.a(((SparseIntArray[]) kVar.f2290c)[7], frameMetrics.getMetric(0));
        }
        if ((i11 & 256) != 0) {
            k.a(((SparseIntArray[]) kVar.f2290c)[8], frameMetrics.getMetric(2));
        }
    }
}

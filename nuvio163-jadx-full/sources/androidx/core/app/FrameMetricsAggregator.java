package androidx.core.app;

import android.util.SparseIntArray;
import java.io.Serializable;

/* JADX INFO: loaded from: classes3.dex */
public class FrameMetricsAggregator {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final k f2248a;

    public FrameMetricsAggregator() {
        this(1);
    }

    public final SparseIntArray[] a() {
        return (SparseIntArray[]) this.f2248a.f2290c;
    }

    /* JADX WARN: Type inference failed for: r1v2, types: [android.util.SparseIntArray[], java.io.Serializable] */
    public final void b() {
        k kVar = this.f2248a;
        Serializable serializable = kVar.f2290c;
        kVar.f2290c = new SparseIntArray[9];
    }

    public FrameMetricsAggregator(int i10) {
        this.f2248a = new k(i10);
    }
}

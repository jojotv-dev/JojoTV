package io.sentry.android.core;

import android.app.Activity;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.SparseIntArray;
import androidx.core.app.FrameMetricsAggregator;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes5.dex */
public final /* synthetic */ class b implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f24616b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ d f24617c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ Activity f24618e;

    public /* synthetic */ b(d dVar, Activity activity, int i10) {
        this.f24616b = i10;
        this.f24617c = dVar;
        this.f24618e = activity;
    }

    @Override // java.lang.Runnable
    public final void run() {
        Activity activity;
        WeakReference weakReference;
        switch (this.f24616b) {
            case 0:
                androidx.core.app.k kVar = ((FrameMetricsAggregator) this.f24617c.f24635a.a()).f2248a;
                kVar.getClass();
                if (androidx.core.app.k.f2286f == null) {
                    HandlerThread handlerThread = new HandlerThread("FrameMetricsAggregator");
                    androidx.core.app.k.f2286f = handlerThread;
                    handlerThread.start();
                    androidx.core.app.k.f2287g = new Handler(androidx.core.app.k.f2286f.getLooper());
                }
                for (int i10 = 0; i10 <= 8; i10++) {
                    SparseIntArray[] sparseIntArrayArr = (SparseIntArray[]) kVar.f2290c;
                    if (sparseIntArrayArr[i10] == null && (kVar.f2289b & (1 << i10)) != 0) {
                        sparseIntArrayArr[i10] = new SparseIntArray();
                    }
                }
                Activity activity2 = this.f24618e;
                activity2.getWindow().addOnFrameMetricsAvailableListener((androidx.core.app.j) kVar.f2292e, androidx.core.app.k.f2287g);
                ((ArrayList) kVar.f2291d).add(new WeakReference(activity2));
                break;
            default:
                androidx.core.app.k kVar2 = ((FrameMetricsAggregator) this.f24617c.f24635a.a()).f2248a;
                ArrayList arrayList = (ArrayList) kVar2.f2291d;
                Iterator it = arrayList.iterator();
                do {
                    boolean zHasNext = it.hasNext();
                    activity = this.f24618e;
                    if (zHasNext) {
                        weakReference = (WeakReference) it.next();
                    }
                    activity.getWindow().removeOnFrameMetricsAvailableListener((androidx.core.app.j) kVar2.f2292e);
                    break;
                } while (weakReference.get() != activity);
                arrayList.remove(weakReference);
                activity.getWindow().removeOnFrameMetricsAvailableListener((androidx.core.app.j) kVar2.f2292e);
                break;
        }
    }
}

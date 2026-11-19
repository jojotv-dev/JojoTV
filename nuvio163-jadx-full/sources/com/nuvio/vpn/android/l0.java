package com.nuvio.vpn.android;

import java.util.concurrent.CopyOnWriteArrayList;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class l0 implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f10919b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Runnable f10920c;

    public /* synthetic */ l0(Runnable runnable, int i10) {
        this.f10919b = i10;
        this.f10920c = runnable;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f10919b) {
            case 0:
                CopyOnWriteArrayList copyOnWriteArrayList = r0.f10986m;
                Runnable runnable = this.f10920c;
                if (copyOnWriteArrayList.contains(runnable)) {
                    runnable.run();
                }
                break;
            default:
                CopyOnWriteArrayList copyOnWriteArrayList2 = r0.f10985l;
                Runnable runnable2 = this.f10920c;
                if (copyOnWriteArrayList2.contains(runnable2)) {
                    runnable2.run();
                }
                break;
        }
    }
}

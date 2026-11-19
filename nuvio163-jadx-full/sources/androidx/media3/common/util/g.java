package androidx.media3.common.util;

import java.util.concurrent.ThreadFactory;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class g implements ThreadFactory {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f2811b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ String f2812c;

    public /* synthetic */ g(String str, int i10) {
        this.f2811b = i10;
        this.f2812c = str;
    }

    @Override // java.util.concurrent.ThreadFactory
    public final Thread newThread(Runnable runnable) {
        switch (this.f2811b) {
            case 0:
                return Util.lambda$newSingleThreadExecutor$3(this.f2812c, runnable);
            default:
                return Util.lambda$newSingleThreadScheduledExecutor$4(this.f2812c, runnable);
        }
    }
}

package m3;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.Choreographer;
import java.util.Random;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class e0 implements Choreographer.FrameCallback {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f34777b = 1;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Object f34778c;

    @Override // android.view.Choreographer.FrameCallback
    public final void doFrame(long j10) {
        switch (this.f34777b) {
            case 0:
                ((Runnable) this.f34778c).run();
                break;
            default:
                (Build.VERSION.SDK_INT >= 28 ? q5.g.a(Looper.getMainLooper()) : new Handler(Looper.getMainLooper())).postDelayed(new q5.f((Context) this.f34778c, 0), new Random().nextInt(Math.max(1000, 1)) + 5000);
                break;
        }
    }
}

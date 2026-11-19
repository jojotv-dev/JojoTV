package fh;

import android.os.Handler;
import android.os.Message;
import eh.h;
import eh.j;
import eh.k;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes5.dex */
public final class d extends k {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final Handler f18061c;

    public d(Handler handler) {
        this.f18061c = handler;
    }

    @Override // eh.k
    public final j a() {
        return new c(this.f18061c);
    }

    @Override // eh.k
    public final gh.b c(Runnable runnable, long j10, TimeUnit timeUnit) {
        if (timeUnit == null) {
            throw new NullPointerException("unit == null");
        }
        Handler handler = this.f18061c;
        h hVar = new h(handler, runnable, 1);
        handler.sendMessageDelayed(Message.obtain(handler, hVar), timeUnit.toMillis(j10));
        return hVar;
    }
}

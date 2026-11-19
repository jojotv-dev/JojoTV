package rh;

import java.util.concurrent.CountDownLatch;

/* JADX INFO: loaded from: classes5.dex */
public final class a extends CountDownLatch implements ih.b, ih.a {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public Throwable f53940b;

    @Override // ih.b
    public final void accept(Object obj) {
        this.f53940b = (Throwable) obj;
        countDown();
    }

    @Override // ih.a
    public final void run() {
        countDown();
    }
}

package zg;

import java.net.InetAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/* JADX INFO: loaded from: classes5.dex */
public final /* synthetic */ class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f66699a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ AtomicReference f66700b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ AtomicInteger f66701c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final /* synthetic */ CountDownLatch f66702d;

    public /* synthetic */ a(AtomicReference atomicReference, AtomicInteger atomicInteger, CountDownLatch countDownLatch, int i10) {
        this.f66699a = i10;
        this.f66700b = atomicReference;
        this.f66701c = atomicInteger;
        this.f66702d = countDownLatch;
    }

    public final void a(InetAddress inetAddress, int i10) {
        switch (this.f66699a) {
            case 0:
                if (inetAddress != null) {
                    this.f66700b.set(inetAddress.getHostAddress());
                    this.f66701c.set(i10);
                }
                this.f66702d.countDown();
                break;
            default:
                if (inetAddress != null) {
                    this.f66700b.set(inetAddress.getHostAddress());
                    this.f66701c.set(i10);
                }
                this.f66702d.countDown();
                break;
        }
    }
}

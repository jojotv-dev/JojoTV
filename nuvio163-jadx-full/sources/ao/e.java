package ao;

import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.chromium.base.ApplicationStatus;
import org.chromium.net.impl.CronetLibraryLoader;
import qh.w;
import up.b3;

/* JADX INFO: loaded from: classes3.dex */
public final class e implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f4156b;

    public /* synthetic */ e(int i10) {
        this.f4156b = i10;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f4156b) {
            case 0:
                CronetLibraryLoader.b();
                break;
            case 1:
            case 2:
                break;
            case 3:
                for (ScheduledThreadPoolExecutor scheduledThreadPoolExecutor : new ArrayList(w.f50791d.keySet())) {
                    if (scheduledThreadPoolExecutor.isShutdown()) {
                        w.f50791d.remove(scheduledThreadPoolExecutor);
                    } else {
                        scheduledThreadPoolExecutor.purge();
                    }
                }
                break;
            case 4:
                b3.f60073d.nextInt();
                b3.f60074e = false;
                break;
            default:
                if (ApplicationStatus.f44095b == null) {
                    uo.c cVar = new uo.c(2);
                    ApplicationStatus.f44095b = cVar;
                    if (ApplicationStatus.f44096c == null) {
                        ApplicationStatus.f44096c = new vn.h();
                    }
                    ApplicationStatus.f44096c.a(cVar);
                    break;
                }
                break;
        }
    }

    public String toString() {
        switch (this.f4156b) {
            case 2:
                return "EmptyRunnable";
            default:
                return super.toString();
        }
    }

    private final void a() {
    }

    private final void b() {
    }
}

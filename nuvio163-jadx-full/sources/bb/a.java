package bb;

import com.liskovsoft.sharedutils.rx.RxHelper;
import eh.d;
import eh.e;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class a implements e, ih.a, ih.b {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f4380b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Runnable f4381c;

    public /* synthetic */ a(Runnable runnable, int i10) {
        this.f4380b = i10;
        this.f4381c = runnable;
    }

    @Override // eh.e
    public void a(d dVar) {
        RxHelper.lambda$fromRunnable$13(this.f4381c, dVar);
    }

    @Override // ih.b
    public void accept(Object obj) {
        this.f4381c.run();
    }

    @Override // ih.a
    public void run() {
        switch (this.f4380b) {
            case 1:
                this.f4381c.run();
                break;
            default:
                RxHelper.lambda$runAsyncUser$10(this.f4381c);
                break;
        }
    }
}

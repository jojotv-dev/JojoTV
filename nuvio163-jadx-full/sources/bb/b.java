package bb;

import com.liskovsoft.sharedutils.rx.OnError;
import com.liskovsoft.sharedutils.rx.RxHelper;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class b implements ih.b {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f4382b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ OnError f4383c;

    public /* synthetic */ b(OnError onError, int i10) {
        this.f4382b = i10;
        this.f4383c = onError;
    }

    @Override // ih.b
    public final void accept(Object obj) {
        switch (this.f4382b) {
            case 0:
                this.f4383c.onError((Throwable) obj);
                break;
            default:
                RxHelper.lambda$runAsyncUser$11(this.f4383c, (Throwable) obj);
                break;
        }
    }
}

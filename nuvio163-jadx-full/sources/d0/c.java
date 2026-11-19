package d0;

import java.util.concurrent.CancellationException;
import q1.x;

/* JADX INFO: loaded from: classes.dex */
public abstract class c extends CancellationException {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f11111b;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ c(String str, int i10) {
        super(str);
        this.f11111b = i10;
    }

    @Override // java.lang.Throwable
    public final Throwable fillInStackTrace() {
        switch (this.f11111b) {
            case 0:
                setStackTrace(d.f11112a);
                break;
            case 1:
                setStackTrace(x.f45499a);
                break;
            default:
                setStackTrace(t2.b.f57150a);
                break;
        }
        return this;
    }
}

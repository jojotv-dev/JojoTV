package androidx.media3.session;

import android.net.Uri;
import java.util.concurrent.Callable;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class e3 implements Callable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3509b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Object f3510c;

    public /* synthetic */ e3(Object obj, int i10) {
        this.f3509b = i10;
        this.f3510c = obj;
    }

    @Override // java.util.concurrent.Callable
    public final Object call() {
        switch (this.f3509b) {
            case 0:
                return SimpleBitmapLoader.load((Uri) this.f3510c);
            default:
                return SimpleBitmapLoader.decode((byte[]) this.f3510c);
        }
    }
}

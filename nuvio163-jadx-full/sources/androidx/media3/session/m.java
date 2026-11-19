package androidx.media3.session;

import java.util.concurrent.Executor;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class m implements Executor {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3591b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Object f3592c;

    public /* synthetic */ m(Object obj, int i10) {
        this.f3591b = i10;
        this.f3592c = obj;
    }

    @Override // java.util.concurrent.Executor
    public final void execute(Runnable runnable) {
        switch (this.f3591b) {
            case 0:
                ((MediaControllerHolder) this.f3592c).lambda$setController$1(runnable);
                break;
            case 1:
                ((MediaLibrarySessionImpl) this.f3592c).postOrRunOnApplicationHandler(runnable);
                break;
            case 2:
                ((MediaNotificationManager) this.f3592c).lambda$new$0(runnable);
                break;
            default:
                ((MediaSessionImpl) this.f3592c).postOrRunOnApplicationHandler(runnable);
                break;
        }
    }
}

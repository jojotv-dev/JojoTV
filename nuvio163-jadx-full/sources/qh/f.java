package qh;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes5.dex */
public final class f implements Future {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final gh.b f50740b;

    public f(gh.b bVar) {
        this.f50740b = bVar;
    }

    @Override // java.util.concurrent.Future
    public final boolean cancel(boolean z2) {
        this.f50740b.dispose();
        return false;
    }

    @Override // java.util.concurrent.Future
    public final Object get() {
        return null;
    }

    @Override // java.util.concurrent.Future
    public final boolean isCancelled() {
        return false;
    }

    @Override // java.util.concurrent.Future
    public final boolean isDone() {
        return false;
    }

    @Override // java.util.concurrent.Future
    public final Object get(long j10, TimeUnit timeUnit) {
        return null;
    }
}

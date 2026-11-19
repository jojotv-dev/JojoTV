package qj;

import java.io.IOException;

/* JADX INFO: loaded from: classes5.dex */
public final class j extends IOException {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final RuntimeException f50840b;

    public j(String str, RuntimeException runtimeException) {
        super(str);
        this.f50840b = runtimeException;
    }

    @Override // java.lang.Throwable
    public final Throwable getCause() {
        return this.f50840b;
    }
}

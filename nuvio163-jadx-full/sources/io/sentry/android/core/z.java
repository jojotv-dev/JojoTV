package io.sentry.android.core;

/* JADX INFO: loaded from: classes5.dex */
public final class z implements io.sentry.hints.a, io.sentry.hints.l {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final boolean f25045b;

    public z(boolean z2) {
        this.f25045b = z2;
    }

    @Override // io.sentry.hints.a
    public final Long b() {
        return null;
    }

    @Override // io.sentry.hints.a
    public final boolean c() {
        return true;
    }

    @Override // io.sentry.hints.a
    public final String e() {
        return this.f25045b ? "anr_background" : "anr_foreground";
    }
}

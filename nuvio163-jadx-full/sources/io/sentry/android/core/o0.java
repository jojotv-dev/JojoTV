package io.sentry.android.core;

/* JADX INFO: loaded from: classes5.dex */
public abstract /* synthetic */ class o0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final /* synthetic */ int[] f24881a;

    static {
        int[] iArr = new int[io.sentry.l0.values().length];
        f24881a = iArr;
        try {
            iArr[io.sentry.l0.DISCONNECTED.ordinal()] = 1;
        } catch (NoSuchFieldError unused) {
        }
        try {
            f24881a[io.sentry.l0.CONNECTED.ordinal()] = 2;
        } catch (NoSuchFieldError unused2) {
        }
    }
}

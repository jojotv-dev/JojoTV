package io.sentry.android.core;

/* JADX INFO: loaded from: classes.dex */
public abstract /* synthetic */ class y {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final /* synthetic */ int[] f25039a;

    static {
        int[] iArr = new int[io.sentry.l0.values().length];
        f25039a = iArr;
        try {
            iArr[io.sentry.l0.CONNECTED.ordinal()] = 1;
        } catch (NoSuchFieldError unused) {
        }
        try {
            f25039a[io.sentry.l0.UNKNOWN.ordinal()] = 2;
        } catch (NoSuchFieldError unused2) {
        }
        try {
            f25039a[io.sentry.l0.NO_PERMISSION.ordinal()] = 3;
        } catch (NoSuchFieldError unused3) {
        }
    }
}

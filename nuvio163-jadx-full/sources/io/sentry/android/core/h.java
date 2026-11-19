package io.sentry.android.core;

import io.sentry.p3;

/* JADX INFO: loaded from: classes5.dex */
public abstract /* synthetic */ class h {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final /* synthetic */ int[] f24672a;

    static {
        int[] iArr = new int[p3.values().length];
        f24672a = iArr;
        try {
            iArr[p3.TRACE.ordinal()] = 1;
        } catch (NoSuchFieldError unused) {
        }
        try {
            f24672a[p3.MANUAL.ordinal()] = 2;
        } catch (NoSuchFieldError unused2) {
        }
    }
}

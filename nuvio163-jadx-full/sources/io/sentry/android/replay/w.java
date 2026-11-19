package io.sentry.android.replay;

import io.sentry.g4;

/* JADX INFO: loaded from: classes5.dex */
public abstract /* synthetic */ class w {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final /* synthetic */ int[] f25346a;

    static {
        int[] iArr = new int[g4.values().length];
        try {
            iArr[g4.CANVAS.ordinal()] = 1;
        } catch (NoSuchFieldError unused) {
        }
        try {
            iArr[g4.PIXEL_COPY.ordinal()] = 2;
        } catch (NoSuchFieldError unused2) {
        }
        f25346a = iArr;
    }
}

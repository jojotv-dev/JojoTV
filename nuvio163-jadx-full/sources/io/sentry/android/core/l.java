package io.sentry.android.core;

import io.sentry.m5;

/* JADX INFO: loaded from: classes.dex */
public abstract /* synthetic */ class l {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final /* synthetic */ int[] f24853a;

    static {
        int[] iArr = new int[m5.values().length];
        f24853a = iArr;
        try {
            iArr[m5.INFO.ordinal()] = 1;
        } catch (NoSuchFieldError unused) {
        }
        try {
            f24853a[m5.WARNING.ordinal()] = 2;
        } catch (NoSuchFieldError unused2) {
        }
        try {
            f24853a[m5.ERROR.ordinal()] = 3;
        } catch (NoSuchFieldError unused3) {
        }
        try {
            f24853a[m5.FATAL.ordinal()] = 4;
        } catch (NoSuchFieldError unused4) {
        }
        try {
            f24853a[m5.DEBUG.ordinal()] = 5;
        } catch (NoSuchFieldError unused5) {
        }
    }
}

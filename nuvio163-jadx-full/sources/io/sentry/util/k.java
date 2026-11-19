package io.sentry.util;

/* JADX INFO: loaded from: classes.dex */
public abstract class k {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final boolean f26262a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final boolean f26263b;

    static {
        try {
            f26262a = "The Android Project".equals(System.getProperty("java.vendor"));
        } catch (Throwable unused) {
            f26262a = false;
        }
        if (f26262a) {
            f26263b = false;
            return;
        }
        try {
            String property = System.getProperty("java.specification.version");
            if (property != null) {
                f26263b = Double.parseDouble(property) >= 9.0d;
            } else {
                f26263b = false;
            }
        } catch (Throwable unused2) {
            f26263b = false;
        }
    }
}

package com.google.common.util.concurrent;

import java.util.concurrent.Executor;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes4.dex */
public final class x implements Executor {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final x f7579b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final /* synthetic */ x[] f7580c;

    static {
        x xVar = new x("INSTANCE", 0);
        f7579b = xVar;
        f7580c = new x[]{xVar};
    }

    public static x valueOf(String str) {
        return (x) Enum.valueOf(x.class, str);
    }

    public static x[] values() {
        return (x[]) f7580c.clone();
    }

    @Override // java.util.concurrent.Executor
    public final void execute(Runnable runnable) {
        runnable.run();
    }

    @Override // java.lang.Enum
    public final String toString() {
        return "MoreExecutors.directExecutor()";
    }
}

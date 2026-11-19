package com.google.common.util.concurrent;

import java.util.concurrent.CopyOnWriteArraySet;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes4.dex */
public final class c0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final c0 f7523b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final CopyOnWriteArraySet f7524c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final /* synthetic */ c0[] f7525e;

    static {
        c0 c0Var = new c0("INSTANCE", 0);
        f7523b = c0Var;
        f7525e = new c0[]{c0Var};
        f7524c = new CopyOnWriteArraySet();
    }

    public static c0 valueOf(String str) {
        return (c0) Enum.valueOf(c0.class, str);
    }

    public static c0[] values() {
        return (c0[]) f7525e.clone();
    }
}

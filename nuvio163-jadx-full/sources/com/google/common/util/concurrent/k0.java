package com.google.common.util.concurrent;

import java.util.concurrent.Executor;
import java.util.concurrent.Future;

/* JADX INFO: loaded from: classes4.dex */
public interface k0 extends Future {
    void addListener(Runnable runnable, Executor executor);
}

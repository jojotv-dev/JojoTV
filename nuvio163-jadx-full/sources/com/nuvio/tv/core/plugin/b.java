package com.nuvio.tv.core.plugin;

import java.util.concurrent.ThreadFactory;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class b implements ThreadFactory {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f7849b;

    public /* synthetic */ b(int i10) {
        this.f7849b = i10;
    }

    @Override // java.util.concurrent.ThreadFactory
    public final Thread newThread(Runnable runnable) {
        switch (this.f7849b) {
            case 0:
                return PluginManager.pluginDispatcher$lambda$0(runnable);
            default:
                return PluginRuntime.httpClient$lambda$0(runnable);
        }
    }
}

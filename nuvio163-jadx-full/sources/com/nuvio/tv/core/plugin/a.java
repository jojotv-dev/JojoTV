package com.nuvio.tv.core.plugin;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class a implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f7847b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Runnable f7848c;

    public /* synthetic */ a(Runnable runnable, int i10) {
        this.f7847b = i10;
        this.f7848c = runnable;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f7847b) {
            case 0:
                PluginManager.pluginDispatcher$lambda$0$0(this.f7848c);
                break;
            default:
                PluginRuntime.httpClient$lambda$0$0(this.f7848c);
                break;
        }
    }
}

package com.liskovsoft.youtubeapi.app.potokennp2.generators;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class g implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f7684b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ PoTokenWebView4 f7685c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ String f7686e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final /* synthetic */ String f7687i;

    public /* synthetic */ g(PoTokenWebView4 poTokenWebView4, String str, String str2, int i10) {
        this.f7684b = i10;
        this.f7685c = poTokenWebView4;
        this.f7686e = str;
        this.f7687i = str2;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f7684b) {
            case 0:
                PoTokenWebView4.downloadAndRunBotguard$lambda$0(this.f7685c, this.f7686e, this.f7687i);
                break;
            default:
                PoTokenWebView4.generatePoToken$lambda$1(this.f7685c, this.f7686e, this.f7687i);
                break;
        }
    }
}

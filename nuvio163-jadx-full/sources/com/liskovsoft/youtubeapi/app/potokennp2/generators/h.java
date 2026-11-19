package com.liskovsoft.youtubeapi.app.potokennp2.generators;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class h implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f7688b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ TvPoTokenWebView4 f7689c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ String f7690e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final /* synthetic */ String f7691i;

    public /* synthetic */ h(TvPoTokenWebView4 tvPoTokenWebView4, String str, String str2, int i10) {
        this.f7688b = i10;
        this.f7689c = tvPoTokenWebView4;
        this.f7690e = str;
        this.f7691i = str2;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f7688b) {
            case 0:
                TvPoTokenWebView4.downloadAndRunBotguard$lambda$2(this.f7689c, this.f7690e, this.f7691i);
                break;
            default:
                TvPoTokenWebView4.generatePoToken$lambda$1(this.f7689c, this.f7690e, this.f7691i);
                break;
        }
    }
}

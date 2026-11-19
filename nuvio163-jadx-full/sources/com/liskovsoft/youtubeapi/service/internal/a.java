package com.liskovsoft.youtubeapi.service.internal;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class a implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f7757b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ MediaServiceData f7758c;

    public /* synthetic */ a(MediaServiceData mediaServiceData, int i10) {
        this.f7757b = i10;
        this.f7758c = mediaServiceData;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f7757b) {
            case 0:
                this.f7758c.lambda$persistNow$0();
                break;
            default:
                this.f7758c.lambda$persistData$1();
                break;
        }
    }
}

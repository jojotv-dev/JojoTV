package com.nuvio.tv.youtube;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class f implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f10409b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ YouTubeAccountViewModel f10410c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ int f10411e;

    public /* synthetic */ f(YouTubeAccountViewModel youTubeAccountViewModel, int i10, int i11) {
        this.f10409b = i11;
        this.f10410c = youTubeAccountViewModel;
        this.f10411e = i10;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f10409b) {
            case 0:
                YouTubeAccountViewModel.check$lambda$0$1(this.f10410c, this.f10411e);
                break;
            default:
                YouTubeAccountViewModel.check$lambda$0(this.f10410c, this.f10411e);
                break;
        }
    }
}

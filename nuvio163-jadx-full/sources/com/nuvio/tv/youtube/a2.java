package com.nuvio.tv.youtube;

import kotlin.jvm.functions.Function0;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class a2 implements Function0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f10328b = 0;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ YtTab f10329c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ YouTubeViewModel f10330e;

    public /* synthetic */ a2(YouTubeViewModel youTubeViewModel, YtTab ytTab) {
        this.f10330e = youTubeViewModel;
        this.f10329c = ytTab;
    }

    @Override // kotlin.jvm.functions.Function0
    public final Object invoke() {
        switch (this.f10328b) {
            case 0:
                return YouTubeViewModel.selectTab$lambda$7(this.f10330e, this.f10329c);
            default:
                return YouTubeViewModel.selectTab$lambda$7$2(this.f10329c, this.f10330e);
        }
    }

    public /* synthetic */ a2(YtTab ytTab, YouTubeViewModel youTubeViewModel) {
        this.f10329c = ytTab;
        this.f10330e = youTubeViewModel;
    }
}

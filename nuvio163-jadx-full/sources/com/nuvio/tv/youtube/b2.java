package com.nuvio.tv.youtube;

import kotlin.jvm.functions.Function0;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class b2 implements Function0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f10352b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ YouTubeViewModel f10353c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ String f10354e;

    public /* synthetic */ b2(YouTubeViewModel youTubeViewModel, String str, int i10) {
        this.f10352b = i10;
        this.f10353c = youTubeViewModel;
        this.f10354e = str;
    }

    @Override // kotlin.jvm.functions.Function0
    public final Object invoke() {
        switch (this.f10352b) {
            case 0:
                return YouTubeViewModel.openChannel$lambda$1(this.f10353c, this.f10354e);
            case 1:
                return YouTubeViewModel.openPlaylistById$lambda$1(this.f10353c, this.f10354e);
            default:
                return YouTubeViewModel.search$lambda$1(this.f10353c, this.f10354e);
        }
    }
}

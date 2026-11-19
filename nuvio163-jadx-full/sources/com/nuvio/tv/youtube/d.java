package com.nuvio.tv.youtube;

import java.util.concurrent.ThreadFactory;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class d implements ThreadFactory {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f10364b;

    public /* synthetic */ d(int i10) {
        this.f10364b = i10;
    }

    @Override // java.util.concurrent.ThreadFactory
    public final Thread newThread(Runnable runnable) {
        switch (this.f10364b) {
            case 0:
                return SponsorBlockSession.worker$lambda$0(runnable);
            case 1:
                return YouTubeCaptionTranslation.writer$lambda$0(runnable);
            default:
                return YouTubeProgress.worker$lambda$0(runnable);
        }
    }
}

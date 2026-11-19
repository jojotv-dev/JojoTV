package androidx.media3.exoplayer.source;

import androidx.media3.common.util.Consumer;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class l implements q7.m {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3287b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ q7.m f3288c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ Consumer f3289e;

    public /* synthetic */ l(q7.m mVar, Consumer consumer, int i10) {
        this.f3287b = i10;
        this.f3288c = mVar;
        this.f3289e = consumer;
    }

    @Override // q7.m
    public final Object get() {
        switch (this.f3287b) {
            case 0:
                return ProgressiveMediaSource.Factory.lambda$setDownloadExecutor$1(this.f3288c, this.f3289e);
            default:
                return SingleSampleMediaSource.Factory.lambda$setDownloadExecutor$0(this.f3288c, this.f3289e);
        }
    }
}

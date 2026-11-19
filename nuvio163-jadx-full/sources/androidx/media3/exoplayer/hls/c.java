package androidx.media3.exoplayer.hls;

import androidx.media3.common.util.Consumer;
import androidx.media3.exoplayer.PlayerMessage;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class c implements Consumer, PlayerMessage.Target {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ Object f3137b;

    public /* synthetic */ c(Object obj) {
        this.f3137b = obj;
    }

    @Override // androidx.media3.common.util.Consumer, ih.b
    public void accept(Object obj) {
        HlsInterstitialsAdsLoader.lambda$startLoadingAssetList$5((HlsInterstitialsAdsLoader.AssetListData) this.f3137b, (HlsInterstitialsAdsLoader.Listener) obj);
    }

    @Override // androidx.media3.exoplayer.PlayerMessage.Target
    public void handleMessage(int i10, Object obj) {
        ((HlsInterstitialsAdsLoader.RunnableAtPosition) this.f3137b).run();
    }
}

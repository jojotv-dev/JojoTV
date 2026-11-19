package androidx.media3.exoplayer;

import androidx.media3.extractor.Extractor;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class t0 implements q7.f {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f3331a;

    public /* synthetic */ t0(int i10) {
        this.f3331a = i10;
    }

    @Override // q7.f
    public final Object apply(Object obj) {
        switch (this.f3331a) {
            case 0:
                return StreamVolumeManager.lambda$release$11((StreamVolumeManager.StreamVolumeState) obj);
            case 1:
                return StreamVolumeManager.lambda$increaseVolume$5((StreamVolumeManager.StreamVolumeState) obj);
            case 2:
                return StreamVolumeManager.lambda$decreaseVolume$7((StreamVolumeManager.StreamVolumeState) obj);
            default:
                return MediaExtractorCompat.lambda$selectExtractor$0((Extractor) obj);
        }
    }
}

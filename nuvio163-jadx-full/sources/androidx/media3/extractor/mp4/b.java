package androidx.media3.extractor.mp4;

import java.util.Comparator;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class b implements Comparator {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3422b;

    public /* synthetic */ b(int i10) {
        this.f3422b = i10;
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        Mp4Extractor.OutOfBandTextSample outOfBandTextSample = (Mp4Extractor.OutOfBandTextSample) obj;
        Mp4Extractor.OutOfBandTextSample outOfBandTextSample2 = (Mp4Extractor.OutOfBandTextSample) obj2;
        switch (this.f3422b) {
            case 0:
                return Mp4Extractor.lambda$orderOutOfBandTextSamples$4(outOfBandTextSample, outOfBandTextSample2);
            default:
                return Mp4Extractor.lambda$countOutOfBandTextRuns$3(outOfBandTextSample, outOfBandTextSample2);
        }
    }
}

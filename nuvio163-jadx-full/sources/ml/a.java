package ml;

import a0.i;
import androidx.media3.extractor.ts.PsExtractor;

/* JADX INFO: loaded from: classes5.dex */
public final class a {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final a f39187b = new a("aimer128f", 128);

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final a f39188c = new a("aimer192f", PsExtractor.AUDIO_STREAM);

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final a f39189d = new a("aimer256f", 256);

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final a f39190e = new a("aimer128s", 128);

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public static final a f39191f = new a("aimer192s", PsExtractor.AUDIO_STREAM);

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public static final a f39192g = new a("aimer256s", 256);

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f39193a;

    public a(String str, int i10) {
        this.f39193a = str;
        if (i10 != 128 && i10 != 192 && i10 != 256) {
            throw new IllegalArgumentException(i.k("unknown security bits: ", i10));
        }
    }
}

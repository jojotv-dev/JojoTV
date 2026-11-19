package pl;

import androidx.media3.extractor.ts.PsExtractor;

/* JADX INFO: loaded from: classes5.dex */
public final class a {

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final a f45189d = new a("faest_128s", 128, 11, 7, 32, 32);

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final a f45190e = new a("faest_128f", 128, 16, 8, 32, 32);

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public static final a f45191f = new a("faest_192s", PsExtractor.AUDIO_STREAM, 16, 12, 48, 40);

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public static final a f45192g = new a("faest_192f", PsExtractor.AUDIO_STREAM, 24, 8, 48, 40);

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public static final a f45193h = new a("faest_256s", 256, 22, 6, 48, 48);

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final a f45194i = new a("faest_256f", 256, 32, 8, 48, 48);

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public static final a f45195j = new a("faest_em_128s", 128, 11, 7, 32, 32);
    public static final a k = new a("faest_em_128f", 128, 16, 8, 32, 32);

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    public static final a f45196l = new a("faest_em_192s", PsExtractor.AUDIO_STREAM, 16, 8, 48, 48);

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final a f45197m = new a("faest_em_192f", PsExtractor.AUDIO_STREAM, 24, 8, 48, 48);

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public static final a f45198n = new a("faest_em_256s", 256, 22, 6, 64, 64);

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public static final a f45199o = new a("faest_em_256f", 256, 32, 8, 64, 64);

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f45200a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f45201b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f45202c;

    public a(String str, int i10, int i11, int i12, int i13, int i14) {
        this.f45200a = str;
        int i15 = i10 - i12;
        int i16 = i15 % i11;
        int i17 = i15 / i11;
        this.f45201b = i13;
        this.f45202c = i14;
    }
}

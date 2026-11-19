package re;

import androidx.media3.extractor.AacUtil;
import kotlin.time.DurationKt;

/* JADX INFO: loaded from: classes4.dex */
public final class q {
    public static final q k = new q(10800000, 6, 60, DurationKt.MILLIS_IN_DAY, 48, 21600000, 320, 4, 6000000, 300000);

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    public static final q f53195l = new q(7200000, 4, 40, 43200000, 24, 10800000, 200, 2, 1500000, AacUtil.AAC_LC_MAX_RATE_BYTES_PER_SECOND);

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final long f53196a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f53197b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f53198c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final long f53199d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final int f53200e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public final long f53201f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public final int f53202g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public final int f53203h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final int f53204i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public final int f53205j;

    public q(long j10, int i10, int i11, long j11, int i12, long j12, int i13, int i14, int i15, int i16) {
        this.f53196a = j10;
        this.f53197b = i10;
        this.f53198c = i11;
        this.f53199d = j11;
        this.f53200e = i12;
        this.f53201f = j12;
        this.f53202g = i13;
        this.f53203h = i14;
        this.f53204i = i15;
        this.f53205j = i16;
    }
}

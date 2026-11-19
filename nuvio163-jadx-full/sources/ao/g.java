package ao;

import java.util.Date;

/* JADX INFO: loaded from: classes5.dex */
public final class g {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final long f4160a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f4161b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final long f4162c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final Long f4163d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final Long f4164e;

    public g(long j10, long j11, long j12, long j13, long j14) {
        this.f4160a = j10;
        this.f4161b = j11;
        this.f4162c = j12;
        this.f4163d = Long.valueOf(j13);
        this.f4164e = Long.valueOf(j14);
    }

    public static Date a(long j10) {
        if (j10 != -1) {
            return new Date(j10);
        }
        return null;
    }
}

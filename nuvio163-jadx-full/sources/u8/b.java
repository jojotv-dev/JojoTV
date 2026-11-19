package u8;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes4.dex */
public final class b {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final long f58302a;

    public b(long j10) {
        this.f58302a = j10;
    }

    public final long a() {
        return TimeUnit.MILLISECONDS.convert((this.f58302a - 116444736000000000L) * 100, TimeUnit.NANOSECONDS);
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj != null && b.class == obj.getClass() && this.f58302a == ((b) obj).f58302a;
    }

    public final int hashCode() {
        long j10 = this.f58302a;
        return (int) (j10 ^ (j10 >>> 32));
    }

    public final String toString() {
        return new Date(a()).toString();
    }
}

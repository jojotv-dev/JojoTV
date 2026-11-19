package io.sentry.android.core.performance;

import android.os.SystemClock;
import io.sentry.t5;

/* JADX INFO: loaded from: classes.dex */
public final class h implements Comparable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public String f24929b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public long f24930c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public long f24931e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public long f24932i;

    public final long a() {
        if (d()) {
            return this.f24932i - this.f24931e;
        }
        return 0L;
    }

    public final t5 b() {
        if (c()) {
            return new t5(this.f24930c * 1000000);
        }
        return null;
    }

    public final boolean c() {
        return this.f24931e != 0;
    }

    @Override // java.lang.Comparable
    public final int compareTo(Object obj) {
        return Long.compare(this.f24930c, ((h) obj).f24930c);
    }

    public final boolean d() {
        return this.f24932i != 0;
    }

    public final void e(long j10) {
        this.f24931e = j10;
        this.f24930c = System.currentTimeMillis() - (SystemClock.uptimeMillis() - this.f24931e);
    }
}

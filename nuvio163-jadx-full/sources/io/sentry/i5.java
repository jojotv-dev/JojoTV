package io.sentry;

import androidx.media3.common.C;
import j$.time.Instant;

/* JADX INFO: loaded from: classes5.dex */
public final class i5 extends r4 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Instant f25614b = Instant.now();

    @Override // io.sentry.r4
    public final long d() {
        Instant instant = this.f25614b;
        return (instant.getEpochSecond() * C.NANOS_PER_SECOND) + ((long) instant.getNano());
    }
}

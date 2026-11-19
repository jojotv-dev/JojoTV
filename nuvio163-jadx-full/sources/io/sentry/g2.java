package io.sentry;

import java.util.Locale;

/* JADX INFO: loaded from: classes5.dex */
public enum g2 implements h2 {
    NANOSECOND,
    MICROSECOND,
    MILLISECOND,
    SECOND,
    MINUTE,
    HOUR,
    DAY,
    WEEK;

    @Override // io.sentry.h2
    public String apiName() {
        return name().toLowerCase(Locale.ROOT);
    }
}

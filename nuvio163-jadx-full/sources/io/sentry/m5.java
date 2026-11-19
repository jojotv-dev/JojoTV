package io.sentry;

import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public enum m5 implements d2 {
    DEBUG,
    INFO,
    WARNING,
    ERROR,
    FATAL;

    @Override // io.sentry.d2
    public void serialize(i3 i3Var, ILogger iLogger) {
        ((v0.m) i3Var).W(name().toLowerCase(Locale.ROOT));
    }
}

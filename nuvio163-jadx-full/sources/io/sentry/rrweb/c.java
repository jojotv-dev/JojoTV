package io.sentry.rrweb;

import io.sentry.ILogger;
import io.sentry.d2;
import io.sentry.i3;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public enum c implements d2 {
    DomContentLoaded,
    Load,
    FullSnapshot,
    IncrementalSnapshot,
    Meta,
    Custom,
    Plugin;

    @Override // io.sentry.d2
    public void serialize(i3 i3Var, ILogger iLogger) throws IOException {
        ((v0.m) i3Var).S(ordinal());
    }
}

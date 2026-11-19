package io.sentry.ndk;

/* JADX INFO: loaded from: classes.dex */
public enum a {
    SENTRY_HANDLER_STRATEGY_DEFAULT(0),
    SENTRY_HANDLER_STRATEGY_CHAIN_AT_START(1);

    private final int value;

    a(int i10) {
        this.value = i10;
    }

    public int getValue() {
        return this.value;
    }
}

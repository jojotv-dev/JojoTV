package io.sentry;

import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public enum h7 implements d2 {
    OK(0, 399),
    CANCELLED(499),
    INTERNAL_ERROR(500),
    UNKNOWN(500),
    UNKNOWN_ERROR(500),
    INVALID_ARGUMENT(400),
    DEADLINE_EXCEEDED(504),
    NOT_FOUND(404),
    ALREADY_EXISTS(409),
    PERMISSION_DENIED(403),
    RESOURCE_EXHAUSTED(429),
    FAILED_PRECONDITION(400),
    ABORTED(409),
    OUT_OF_RANGE(400),
    UNIMPLEMENTED(501),
    UNAVAILABLE(503),
    DATA_LOSS(500),
    UNAUTHENTICATED(401);

    private final int maxHttpStatusCode;
    private final int minHttpStatusCode;

    h7(int i10) {
        this.minHttpStatusCode = i10;
        this.maxHttpStatusCode = i10;
    }

    public static h7 fromApiNameSafely(String str) {
        if (str == null) {
            return null;
        }
        try {
            return valueOf(str.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException unused) {
            return null;
        }
    }

    public static h7 fromHttpStatusCode(int i10) {
        for (h7 h7Var : values()) {
            if (h7Var.matches(i10)) {
                return h7Var;
            }
        }
        return null;
    }

    private boolean matches(int i10) {
        return i10 >= this.minHttpStatusCode && i10 <= this.maxHttpStatusCode;
    }

    public String apiName() {
        return name().toLowerCase(Locale.ROOT);
    }

    @Override // io.sentry.d2
    public void serialize(i3 i3Var, ILogger iLogger) {
        ((v0.m) i3Var).W(apiName());
    }

    public static h7 fromHttpStatusCode(Integer num, h7 h7Var) {
        h7 h7VarFromHttpStatusCode = num != null ? fromHttpStatusCode(num.intValue()) : h7Var;
        return h7VarFromHttpStatusCode != null ? h7VarFromHttpStatusCode : h7Var;
    }

    h7(int i10, int i11) {
        this.minHttpStatusCode = i10;
        this.maxHttpStatusCode = i11;
    }
}

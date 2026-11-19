package io.sentry;

import java.util.Objects;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes.dex */
public final class f0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f25538a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Pattern f25539b;

    public f0(String str) {
        Pattern patternCompile;
        this.f25538a = str;
        try {
            patternCompile = Pattern.compile(str);
        } catch (Throwable unused) {
            k4.b().getOptions().getLogger().k(m5.DEBUG, "Only using filter string for String comparison as it could not be parsed as regex: %s", str);
            patternCompile = null;
        }
        this.f25539b = patternCompile;
    }

    public final boolean equals(Object obj) {
        if (obj == null || f0.class != obj.getClass()) {
            return false;
        }
        return Objects.equals(this.f25538a, ((f0) obj).f25538a);
    }

    public final int hashCode() {
        return Objects.hash(this.f25538a);
    }
}

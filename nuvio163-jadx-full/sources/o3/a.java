package o3;

import java.util.Locale;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Locale f43450a;

    public a(Locale locale) {
        this.f43450a = locale;
    }

    public final boolean equals(Object obj) {
        if (obj == null || !(obj instanceof a)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        return Intrinsics.areEqual(this.f43450a.toLanguageTag(), ((a) obj).f43450a.toLanguageTag());
    }

    public final int hashCode() {
        return this.f43450a.toLanguageTag().hashCode();
    }

    public final String toString() {
        return this.f43450a.toLanguageTag();
    }
}

package ld;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class x4 implements y4 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f33204a;

    public x4(String value) {
        Intrinsics.checkNotNullParameter(value, "value");
        this.f33204a = value;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof x4) && Intrinsics.areEqual(this.f33204a, ((x4) obj).f33204a);
    }

    public final int hashCode() {
        return this.f33204a.hashCode();
    }

    public final String toString() {
        return "ApiKey()";
    }
}

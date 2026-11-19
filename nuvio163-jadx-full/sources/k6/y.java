package k6;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class y {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f27954a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f27955b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final String f27956c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final String f27957d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final String f27958e;

    public y(String str, String str2, String str3, String str4, String str5) {
        this.f27954a = str;
        this.f27955b = str2;
        this.f27956c = str3;
        this.f27957d = str4;
        this.f27958e = str5;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof y) && Intrinsics.areEqual(((y) obj).f27954a, this.f27954a);
    }

    public final int hashCode() {
        return this.f27954a.hashCode();
    }

    public final String toString() {
        return this.f27954a;
    }
}

package com.nuvio.tv.data.local;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class j5 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f8756a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final boolean f8757b;

    public j5(String languageCode, boolean z2) {
        Intrinsics.checkNotNullParameter(languageCode, "languageCode");
        this.f8756a = languageCode;
        this.f8757b = z2;
    }

    public final String a() {
        return this.f8756a;
    }

    public final boolean b() {
        return this.f8757b;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof j5)) {
            return false;
        }
        j5 j5Var = (j5) obj;
        return Intrinsics.areEqual(this.f8756a, j5Var.f8756a) && this.f8757b == j5Var.f8757b;
    }

    public final int hashCode() {
        return Boolean.hashCode(this.f8757b) + (this.f8756a.hashCode() * 31);
    }

    public final String toString() {
        return "ResolvedSubtitlePreferredLanguage(languageCode=" + this.f8756a + ", isSystemDefault=" + this.f8757b + ")";
    }
}

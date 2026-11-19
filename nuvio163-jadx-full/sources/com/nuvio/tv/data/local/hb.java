package com.nuvio.tv.data.local;

import java.util.Map;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class hb {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Map f8589a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Map f8590b;

    public hb(Map recent, Map archive) {
        Intrinsics.checkNotNullParameter(recent, "recent");
        Intrinsics.checkNotNullParameter(archive, "archive");
        this.f8589a = recent;
        this.f8590b = archive;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof hb)) {
            return false;
        }
        hb hbVar = (hb) obj;
        return Intrinsics.areEqual(this.f8589a, hbVar.f8589a) && Intrinsics.areEqual(this.f8590b, hbVar.f8590b);
    }

    public final int hashCode() {
        return this.f8590b.hashCode() + (this.f8589a.hashCode() * 31);
    }

    public final String toString() {
        return "WatchProgressBuckets(recent=" + this.f8589a + ", archive=" + this.f8590b + ")";
    }
}

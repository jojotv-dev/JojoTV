package nf;

import java.util.Arrays;
import java.util.LinkedHashMap;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class z2 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final long f42719a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final LinkedHashMap f42720b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final Long f42721c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final byte[] f42722d;

    public z2(long j10, LinkedHashMap directPositions, Long l7, byte[] initialBytes) {
        Intrinsics.checkNotNullParameter(directPositions, "directPositions");
        Intrinsics.checkNotNullParameter(initialBytes, "initialBytes");
        this.f42719a = j10;
        this.f42720b = directPositions;
        this.f42721c = l7;
        this.f42722d = initialBytes;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof z2)) {
            return false;
        }
        z2 z2Var = (z2) obj;
        return this.f42719a == z2Var.f42719a && Intrinsics.areEqual(this.f42720b, z2Var.f42720b) && Intrinsics.areEqual(this.f42721c, z2Var.f42721c) && Intrinsics.areEqual(this.f42722d, z2Var.f42722d);
    }

    public final int hashCode() {
        int iHashCode = (this.f42720b.hashCode() + (Long.hashCode(this.f42719a) * 31)) * 31;
        Long l7 = this.f42721c;
        return Arrays.hashCode(this.f42722d) + ((iHashCode + (l7 == null ? 0 : l7.hashCode())) * 31);
    }

    public final String toString() {
        return "InitialMetadata(segmentDataStart=" + this.f42719a + ", directPositions=" + this.f42720b + ", totalLength=" + this.f42721c + ", initialBytes=" + Arrays.toString(this.f42722d) + ")";
    }
}

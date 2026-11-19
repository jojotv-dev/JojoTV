package hf;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class pa extends sa {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f22544a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final boolean f22545b;

    public pa(String episodeId, boolean z2) {
        Intrinsics.checkNotNullParameter(episodeId, "episodeId");
        this.f22544a = episodeId;
        this.f22545b = z2;
    }

    public final boolean a() {
        return this.f22545b;
    }

    public final String b() {
        return this.f22544a;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof pa)) {
            return false;
        }
        pa paVar = (pa) obj;
        return Intrinsics.areEqual(this.f22544a, paVar.f22544a) && this.f22545b == paVar.f22545b;
    }

    public final int hashCode() {
        return Boolean.hashCode(this.f22545b) + (this.f22544a.hashCode() * 31);
    }

    public final String toString() {
        return "RestoreEpisode(episodeId=" + this.f22544a + ", consumeRequest=" + this.f22545b + ")";
    }
}

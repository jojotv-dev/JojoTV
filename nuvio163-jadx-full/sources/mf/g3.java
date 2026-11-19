package mf;

import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class g3 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final List f36310a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final List f36311b;

    public g3(List audioTracks, List subtitleTracks) {
        Intrinsics.checkNotNullParameter(audioTracks, "audioTracks");
        Intrinsics.checkNotNullParameter(subtitleTracks, "subtitleTracks");
        this.f36310a = audioTracks;
        this.f36311b = subtitleTracks;
    }

    public final List a() {
        return this.f36310a;
    }

    public final List b() {
        return this.f36311b;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof g3)) {
            return false;
        }
        g3 g3Var = (g3) obj;
        return Intrinsics.areEqual(this.f36310a, g3Var.f36310a) && Intrinsics.areEqual(this.f36311b, g3Var.f36311b);
    }

    public final int hashCode() {
        return this.f36311b.hashCode() + (this.f36310a.hashCode() * 31);
    }

    public final String toString() {
        return "MpvTrackSnapshot(audioTracks=" + this.f36310a + ", subtitleTracks=" + this.f36311b + ")";
    }
}

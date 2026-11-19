package ne;

import java.util.ArrayList;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class d {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f40182a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final ArrayList f40183b;

    public d(String query, ArrayList channels) {
        Intrinsics.checkNotNullParameter(query, "query");
        Intrinsics.checkNotNullParameter(channels, "channels");
        this.f40182a = query;
        this.f40183b = channels;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof d)) {
            return false;
        }
        d dVar = (d) obj;
        return Intrinsics.areEqual(this.f40182a, dVar.f40182a) && Intrinsics.areEqual(this.f40183b, dVar.f40183b);
    }

    public final int hashCode() {
        return this.f40183b.hashCode() + (this.f40182a.hashCode() * 31);
    }

    public final String toString() {
        return "ChannelPickerSearchResult(query=" + this.f40182a + ", channels=" + this.f40183b + ")";
    }
}

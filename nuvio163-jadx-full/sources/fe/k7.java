package fe;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class k7 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final LinkedHashMap f17094a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final LinkedHashMap f17095b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final String f17096c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final HashSet f17097d;

    public k7(LinkedHashMap channels, LinkedHashMap programs, String str, HashSet programChannelIds) {
        Intrinsics.checkNotNullParameter(channels, "channels");
        Intrinsics.checkNotNullParameter(programs, "programs");
        Intrinsics.checkNotNullParameter(programChannelIds, "programChannelIds");
        this.f17094a = channels;
        this.f17095b = programs;
        this.f17096c = str;
        this.f17097d = programChannelIds;
    }

    public final String a() {
        return this.f17096c;
    }

    public final Set b() {
        return this.f17097d;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof k7)) {
            return false;
        }
        k7 k7Var = (k7) obj;
        return Intrinsics.areEqual(this.f17094a, k7Var.f17094a) && Intrinsics.areEqual(this.f17095b, k7Var.f17095b) && Intrinsics.areEqual(this.f17096c, k7Var.f17096c) && Intrinsics.areEqual(this.f17097d, k7Var.f17097d);
    }

    public final int hashCode() {
        int iHashCode = (this.f17095b.hashCode() + (this.f17094a.hashCode() * 31)) * 31;
        String str = this.f17096c;
        return this.f17097d.hashCode() + ((iHashCode + (str == null ? 0 : str.hashCode())) * 31);
    }

    public final String toString() {
        return "XmltvResult(channels=" + this.f17094a + ", programs=" + this.f17095b + ", error=" + this.f17096c + ", programChannelIds=" + this.f17097d + ")";
    }
}

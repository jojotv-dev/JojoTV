package no;

import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes5.dex */
public final class e {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final oo.e f43071a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final oo.e f43072b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final List f43073c;

    public e(oo.e currentConstraints, oo.e nextConstraints, List markersStack) {
        Intrinsics.checkNotNullParameter(currentConstraints, "currentConstraints");
        Intrinsics.checkNotNullParameter(nextConstraints, "nextConstraints");
        Intrinsics.checkNotNullParameter(markersStack, "markersStack");
        this.f43071a = currentConstraints;
        this.f43072b = nextConstraints;
        this.f43073c = markersStack;
    }

    public final boolean equals(Object obj) {
        e eVar = obj instanceof e ? (e) obj : null;
        return eVar != null && Intrinsics.areEqual(this.f43071a, eVar.f43071a) && Intrinsics.areEqual(this.f43072b, eVar.f43072b) && Intrinsics.areEqual(this.f43073c, eVar.f43073c);
    }

    public final int hashCode() {
        return this.f43073c.hashCode() + ((this.f43072b.hashCode() + (this.f43071a.hashCode() * 37)) * 37);
    }
}

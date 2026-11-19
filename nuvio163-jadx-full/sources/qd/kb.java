package qd;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class kb {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f46987a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f46988b;

    public kb(int i10, String contentId) {
        Intrinsics.checkNotNullParameter(contentId, "contentId");
        this.f46987a = i10;
        this.f46988b = contentId;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof kb)) {
            return false;
        }
        kb kbVar = (kb) obj;
        return this.f46987a == kbVar.f46987a && Intrinsics.areEqual(this.f46988b, kbVar.f46988b);
    }

    public final int hashCode() {
        return this.f46988b.hashCode() + (Integer.hashCode(this.f46987a) * 31);
    }

    public final String toString() {
        return "ProfileContentKey(profileId=" + this.f46987a + ", contentId=" + this.f46988b + ")";
    }
}

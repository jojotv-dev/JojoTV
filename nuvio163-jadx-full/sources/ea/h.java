package ea;

import java.util.Objects;

/* JADX INFO: loaded from: classes4.dex */
public final class h {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f14052a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f14053b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final String f14054c;

    public h(String str, int i10) {
        this.f14052a = str == null ? "" : str.toLowerCase();
        this.f14053b = i10;
        this.f14054c = "";
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof h)) {
            return false;
        }
        h hVar = (h) obj;
        return this.f14052a.equals(hVar.f14052a) && this.f14053b == hVar.f14053b && this.f14054c.equals(hVar.f14054c);
    }

    public final int hashCode() {
        int i10 = this.f14053b;
        return Objects.hash(this.f14052a, i10 == 0 ? null : Integer.valueOf(i10 - 1), this.f14054c);
    }
}

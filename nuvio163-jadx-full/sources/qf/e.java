package qf;

/* JADX INFO: loaded from: classes4.dex */
public final class e {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final boolean f50462a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f50463b;

    public e(boolean z2, int i10) {
        this.f50462a = z2;
        this.f50463b = i10;
    }

    public final int a() {
        return this.f50463b;
    }

    public final boolean b() {
        return this.f50462a;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof e)) {
            return false;
        }
        e eVar = (e) obj;
        return this.f50462a == eVar.f50462a && this.f50463b == eVar.f50463b;
    }

    public final int hashCode() {
        return Integer.hashCode(this.f50463b) + (Boolean.hashCode(this.f50462a) * 31);
    }

    public final String toString() {
        return "KeyboardVisibilityState(isVisible=" + this.f50462a + ", bottomPx=" + this.f50463b + ")";
    }
}

package h3;

/* JADX INFO: loaded from: classes.dex */
public final class y {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final y f20773c = new y(0, false);

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final boolean f20774a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f20775b;

    public y() {
        this.f20774a = false;
        this.f20775b = 0;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof y)) {
            return false;
        }
        y yVar = (y) obj;
        return this.f20774a == yVar.f20774a && this.f20775b == yVar.f20775b;
    }

    public final int hashCode() {
        return Integer.hashCode(this.f20775b) + (Boolean.hashCode(this.f20774a) * 31);
    }

    public final String toString() {
        return "PlatformParagraphStyle(includeFontPadding=" + this.f20774a + ", emojiSupportMatch=" + ((Object) k.a(this.f20775b)) + ')';
    }

    public y(int i10, boolean z2) {
        this.f20774a = z2;
        this.f20775b = i10;
    }
}

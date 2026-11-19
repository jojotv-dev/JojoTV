package r7;

/* JADX INFO: loaded from: classes4.dex */
public final class l0 extends m0 {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final l0 f51298e = new l0("", 0);

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final l0 f51299i = new l0("", 1);

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ int f51300c;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ l0(Comparable comparable, int i10) {
        super(comparable);
        this.f51300c = i10;
    }

    @Override // r7.m0
    /* JADX INFO: renamed from: a */
    public int compareTo(m0 m0Var) {
        switch (this.f51300c) {
            case 0:
                return m0Var == this ? 0 : 1;
            case 1:
                return m0Var == this ? 0 : -1;
            default:
                return super.compareTo(m0Var);
        }
    }

    @Override // r7.m0
    public final void b(StringBuilder sb2) {
        switch (this.f51300c) {
            case 0:
                throw new AssertionError();
            case 1:
                sb2.append("(-∞");
                return;
            default:
                sb2.append('[');
                sb2.append(this.f51309b);
                return;
        }
    }

    @Override // r7.m0
    public final void c(StringBuilder sb2) {
        switch (this.f51300c) {
            case 0:
                sb2.append("+∞)");
                return;
            case 1:
                throw new AssertionError();
            default:
                sb2.append(this.f51309b);
                sb2.append(')');
                return;
        }
    }

    @Override // r7.m0, java.lang.Comparable
    public int compareTo(Object obj) {
        switch (this.f51300c) {
            case 0:
                return ((m0) obj) == this ? 0 : 1;
            case 1:
                return ((m0) obj) == this ? 0 : -1;
            default:
                return super.compareTo(obj);
        }
    }

    @Override // r7.m0
    public Comparable d() {
        switch (this.f51300c) {
            case 0:
                throw new IllegalStateException("range unbounded on this side");
            case 1:
                throw new IllegalStateException("range unbounded on this side");
            default:
                return super.d();
        }
    }

    /* JADX WARN: Type inference failed for: r0v2, types: [java.lang.Comparable, java.lang.Object] */
    @Override // r7.m0
    public final boolean e(Comparable comparable) {
        switch (this.f51300c) {
            case 0:
                return false;
            case 1:
                return true;
            default:
                g2 g2Var = g2.f51262e;
                return this.f51309b.compareTo(comparable) <= 0;
        }
    }

    @Override // r7.m0
    public final int hashCode() {
        switch (this.f51300c) {
            case 0:
                return System.identityHashCode(this);
            case 1:
                return System.identityHashCode(this);
            default:
                return this.f51309b.hashCode();
        }
    }

    public final String toString() {
        switch (this.f51300c) {
            case 0:
                return "+∞";
            case 1:
                return "-∞";
            default:
                return "\\" + this.f51309b + "/";
        }
    }
}

package r7;

import java.io.Serializable;

/* JADX INFO: loaded from: classes.dex */
public final class e2 extends f2 implements Serializable {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final e2 f51241c = new e2(0);

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final e2 f51242e = new e2(1);

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f51243b;

    public /* synthetic */ e2(int i10) {
        this.f51243b = i10;
    }

    @Override // r7.f2
    public final f2 a() {
        switch (this.f51243b) {
            case 0:
                return f51242e;
            default:
                return f51241c;
        }
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        switch (this.f51243b) {
            case 0:
                Comparable comparable = (Comparable) obj;
                Comparable comparable2 = (Comparable) obj2;
                comparable.getClass();
                comparable2.getClass();
                return comparable.compareTo(comparable2);
            default:
                Comparable comparable3 = (Comparable) obj;
                Comparable comparable4 = (Comparable) obj2;
                comparable3.getClass();
                if (comparable3 == comparable4) {
                    return 0;
                }
                return comparable4.compareTo(comparable3);
        }
    }

    public final String toString() {
        switch (this.f51243b) {
            case 0:
                return "Ordering.natural()";
            default:
                return "Ordering.natural().reverse()";
        }
    }
}

package m0;

import kotlin.jvm.functions.Function1;

/* JADX INFO: loaded from: classes.dex */
public final class a1 {

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final a1 f34247d = new a1(null, null, null, 63);

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Function1 f34248a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Function1 f34249b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final Function1 f34250c;

    public a1(Function1 function1, Function1 function2, Function1 function3, int i10) {
        function1 = (i10 & 1) != 0 ? null : function1;
        function2 = (i10 & 4) != 0 ? null : function2;
        function3 = (i10 & 16) != 0 ? null : function3;
        this.f34248a = function1;
        this.f34249b = function2;
        this.f34250c = function3;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof a1)) {
            return false;
        }
        a1 a1Var = (a1) obj;
        return this.f34248a == a1Var.f34248a && this.f34249b == a1Var.f34249b && this.f34250c == a1Var.f34250c;
    }

    public final int hashCode() {
        Function1 function1 = this.f34248a;
        int iHashCode = (function1 != null ? function1.hashCode() : 0) * 961;
        Function1 function2 = this.f34249b;
        int iHashCode2 = (iHashCode + (function2 != null ? function2.hashCode() : 0)) * 961;
        Function1 function3 = this.f34250c;
        return (iHashCode2 + (function3 != null ? function3.hashCode() : 0)) * 31;
    }
}

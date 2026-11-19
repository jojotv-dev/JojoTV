package r7;

import java.util.Iterator;
import java.util.NoSuchElementException;

/* JADX INFO: loaded from: classes4.dex */
public final class p1 extends a3 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public int f51334b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public Object f51335c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ int f51336e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final Iterator f51337i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public final /* synthetic */ Object f51338m;

    public p1() {
        this.f51334b = 2;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // java.util.Iterator
    public final boolean hasNext() {
        Object next;
        l3.c.A(this.f51334b != 4);
        int iB = u4.i.b(this.f51334b);
        if (iB == 0) {
            return true;
        }
        if (iB != 2) {
            this.f51334b = 4;
            switch (this.f51336e) {
                case 0:
                    do {
                        Iterator it = this.f51337i;
                        if (!it.hasNext()) {
                            this.f51334b = 3;
                            next = null;
                        } else {
                            next = it.next();
                        }
                        break;
                    } while (!((q7.i) this.f51338m).apply(next));
                    break;
                default:
                    do {
                        Iterator it2 = this.f51337i;
                        if (!it2.hasNext()) {
                            this.f51334b = 3;
                            next = null;
                        } else {
                            next = it2.next();
                        }
                        break;
                    } while (!((q2) this.f51338m).f51345c.contains(next));
                    break;
            }
            this.f51335c = next;
            if (this.f51334b != 3) {
                this.f51334b = 1;
                return true;
            }
        }
        return false;
    }

    @Override // java.util.Iterator
    public final Object next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        this.f51334b = 2;
        Object obj = this.f51335c;
        this.f51335c = null;
        return obj;
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public p1(Iterator it, q7.i iVar) {
        this();
        this.f51336e = 0;
        this.f51337i = it;
        this.f51338m = iVar;
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public p1(q2 q2Var) {
        this();
        this.f51336e = 1;
        this.f51338m = q2Var;
        this.f51337i = q2Var.f51344b.iterator();
    }
}

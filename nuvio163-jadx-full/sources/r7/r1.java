package r7;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.NoSuchElementException;

/* JADX INFO: loaded from: classes4.dex */
public final class r1 implements Iterator {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public Iterator f51346b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public Iterator f51347c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public Iterator f51348e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public ArrayDeque f51349i;

    @Override // java.util.Iterator
    public final boolean hasNext() {
        Iterator it;
        while (true) {
            Iterator it2 = this.f51347c;
            it2.getClass();
            if (it2.hasNext()) {
                return true;
            }
            while (true) {
                Iterator it3 = this.f51348e;
                if (it3 != null && it3.hasNext()) {
                    it = this.f51348e;
                    break;
                }
                ArrayDeque arrayDeque = this.f51349i;
                if (arrayDeque == null || arrayDeque.isEmpty()) {
                    it = null;
                    break;
                }
                this.f51348e = (Iterator) this.f51349i.removeFirst();
            }
            this.f51348e = it;
            if (it == null) {
                return false;
            }
            Iterator it4 = (Iterator) it.next();
            this.f51347c = it4;
            if (it4 instanceof r1) {
                r1 r1Var = (r1) it4;
                this.f51347c = r1Var.f51347c;
                if (this.f51349i == null) {
                    this.f51349i = new ArrayDeque();
                }
                this.f51349i.addFirst(this.f51348e);
                if (r1Var.f51349i != null) {
                    while (!r1Var.f51349i.isEmpty()) {
                        this.f51349i.addFirst((Iterator) r1Var.f51349i.removeLast());
                    }
                }
                this.f51348e = r1Var.f51348e;
            }
        }
    }

    @Override // java.util.Iterator
    public final Object next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        Iterator it = this.f51347c;
        this.f51346b = it;
        return it.next();
    }

    @Override // java.util.Iterator
    public final void remove() {
        Iterator it = this.f51346b;
        if (it == null) {
            throw new IllegalStateException("no calls to next() since the last call to remove()");
        }
        it.remove();
        this.f51346b = null;
    }
}

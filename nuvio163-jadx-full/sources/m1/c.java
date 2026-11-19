package m1;

import java.util.NoSuchElementException;

/* JADX INFO: loaded from: classes.dex */
public final class c extends a {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ int f34700e = 0;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final Object f34701i;

    public c(int i10, int i11, Object[] objArr) {
        super(i10, i11);
        this.f34701i = objArr;
    }

    @Override // java.util.ListIterator, java.util.Iterator
    public final Object next() {
        switch (this.f34700e) {
            case 0:
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                Object[] objArr = (Object[]) this.f34701i;
                int i10 = this.f34698b;
                this.f34698b = i10 + 1;
                return objArr[i10];
            default:
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                this.f34698b++;
                return this.f34701i;
        }
    }

    @Override // java.util.ListIterator
    public final Object previous() {
        switch (this.f34700e) {
            case 0:
                if (!hasPrevious()) {
                    throw new NoSuchElementException();
                }
                Object[] objArr = (Object[]) this.f34701i;
                int i10 = this.f34698b - 1;
                this.f34698b = i10;
                return objArr[i10];
            default:
                if (!hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.f34698b--;
                return this.f34701i;
        }
    }

    public c(Object obj, int i10) {
        super(i10, 1);
        this.f34701i = obj;
    }
}

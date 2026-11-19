package r7;

import java.io.Serializable;
import java.util.AbstractSequentialList;
import java.util.List;
import java.util.ListIterator;

/* JADX INFO: loaded from: classes4.dex */
public final class y1 extends AbstractSequentialList implements Serializable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final List f51381b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final q7.f f51382c;

    public y1(List list, q7.f fVar) {
        list.getClass();
        this.f51381b = list;
        this.f51382c = fVar;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public final boolean isEmpty() {
        return this.f51381b.isEmpty();
    }

    @Override // java.util.AbstractSequentialList, java.util.AbstractList, java.util.List
    public final ListIterator listIterator(int i10) {
        return new x1(this, this.f51381b.listIterator(i10));
    }

    @Override // java.util.AbstractList
    public final void removeRange(int i10, int i11) {
        this.f51381b.subList(i10, i11).clear();
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public final int size() {
        return this.f51381b.size();
    }
}

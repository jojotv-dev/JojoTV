package r7;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;

/* JADX INFO: loaded from: classes4.dex */
public final class s2 extends r2 implements SortedSet {
    public s2(SortedSet sortedSet, q7.i iVar) {
        super(sortedSet, iVar);
    }

    @Override // java.util.SortedSet
    public final Comparator comparator() {
        return ((SortedSet) this.f51350b).comparator();
    }

    @Override // java.util.SortedSet
    public final Object first() {
        Iterator it = this.f51350b.iterator();
        it.getClass();
        q7.i iVar = this.f51351c;
        iVar.getClass();
        while (it.hasNext()) {
            Object next = it.next();
            if (iVar.apply(next)) {
                return next;
            }
        }
        throw new NoSuchElementException();
    }

    @Override // java.util.SortedSet
    public final SortedSet headSet(Object obj) {
        return new s2((Set) ((SortedSet) this.f51350b).headSet(obj), this.f51351c);
    }

    @Override // java.util.SortedSet
    public final Object last() {
        SortedSet sortedSetHeadSet = (SortedSet) this.f51350b;
        while (true) {
            Object objLast = sortedSetHeadSet.last();
            if (this.f51351c.apply(objLast)) {
                return objLast;
            }
            sortedSetHeadSet = sortedSetHeadSet.headSet(objLast);
        }
    }

    @Override // java.util.SortedSet
    public final SortedSet subSet(Object obj, Object obj2) {
        return new s2((Set) ((SortedSet) this.f51350b).subSet(obj, obj2), this.f51351c);
    }

    @Override // java.util.SortedSet
    public final SortedSet tailSet(Object obj) {
        return new s2((Set) ((SortedSet) this.f51350b).tailSet(obj), this.f51351c);
    }
}

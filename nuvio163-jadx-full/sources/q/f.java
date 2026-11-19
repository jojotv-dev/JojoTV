package q;

import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

/* JADX INFO: loaded from: classes.dex */
public class f implements Iterable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public c f45275b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public c f45276c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final WeakHashMap f45277e = new WeakHashMap();

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public int f45278i = 0;

    public c a(Object obj) {
        c cVar = this.f45275b;
        while (cVar != null && !cVar.f45268b.equals(obj)) {
            cVar = cVar.f45270e;
        }
        return cVar;
    }

    public Object b(Object obj) {
        c cVarA = a(obj);
        if (cVarA == null) {
            return null;
        }
        this.f45278i--;
        WeakHashMap weakHashMap = this.f45277e;
        if (!weakHashMap.isEmpty()) {
            Iterator it = weakHashMap.keySet().iterator();
            while (it.hasNext()) {
                ((e) it.next()).a(cVarA);
            }
        }
        c cVar = cVarA.f45271i;
        if (cVar != null) {
            cVar.f45270e = cVarA.f45270e;
        } else {
            this.f45275b = cVarA.f45270e;
        }
        c cVar2 = cVarA.f45270e;
        if (cVar2 != null) {
            cVar2.f45271i = cVar;
        } else {
            this.f45276c = cVar;
        }
        cVarA.f45270e = null;
        cVarA.f45271i = null;
        return cVarA.f45269c;
    }

    public final boolean equals(Object obj) {
        b bVar;
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof f)) {
            return false;
        }
        f fVar = (f) obj;
        if (this.f45278i != fVar.f45278i) {
            return false;
        }
        Iterator it = iterator();
        Iterator it2 = fVar.iterator();
        while (true) {
            bVar = (b) it;
            if (!bVar.hasNext()) {
                break;
            }
            b bVar2 = (b) it2;
            if (!bVar2.hasNext()) {
                break;
            }
            Map.Entry entry = (Map.Entry) bVar.next();
            Object next = bVar2.next();
            if ((entry == null && next != null) || (entry != null && !entry.equals(next))) {
                return false;
            }
        }
        return (bVar.hasNext() || ((b) it2).hasNext()) ? false : true;
    }

    public final int hashCode() {
        Iterator it = iterator();
        int iHashCode = 0;
        while (true) {
            b bVar = (b) it;
            if (!bVar.hasNext()) {
                return iHashCode;
            }
            iHashCode += ((Map.Entry) bVar.next()).hashCode();
        }
    }

    @Override // java.lang.Iterable
    public final Iterator iterator() {
        b bVar = new b(this.f45275b, this.f45276c, 0);
        this.f45277e.put(bVar, Boolean.FALSE);
        return bVar;
    }

    public final String toString() {
        StringBuilder sb2 = new StringBuilder("[");
        Iterator it = iterator();
        while (true) {
            b bVar = (b) it;
            if (!bVar.hasNext()) {
                sb2.append("]");
                return sb2.toString();
            }
            sb2.append(((Map.Entry) bVar.next()).toString());
            if (bVar.hasNext()) {
                sb2.append(", ");
            }
        }
    }
}

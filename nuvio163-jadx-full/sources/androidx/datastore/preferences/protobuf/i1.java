package androidx.datastore.preferences.protobuf;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;

/* JADX INFO: loaded from: classes3.dex */
public class i1 extends AbstractSet {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f2488b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ AbstractMap f2489c;

    public /* synthetic */ i1(AbstractMap abstractMap, int i10) {
        this.f2488b = i10;
        this.f2489c = abstractMap;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public boolean add(Object obj) {
        switch (this.f2488b) {
            case 0:
                Map.Entry entry = (Map.Entry) obj;
                if (contains(entry)) {
                    return false;
                }
                ((c1) this.f2489c).put((Comparable) entry.getKey(), entry.getValue());
                return true;
            default:
                return super.add(obj);
        }
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public final void clear() {
        switch (this.f2488b) {
            case 0:
                ((c1) this.f2489c).clear();
                break;
            default:
                ((y7.m) this.f2489c).clear();
                break;
        }
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public final boolean contains(Object obj) {
        switch (this.f2488b) {
            case 0:
                Map.Entry entry = (Map.Entry) obj;
                Object obj2 = ((c1) this.f2489c).get(entry.getKey());
                Object value = entry.getValue();
                return obj2 == value || (obj2 != null && obj2.equals(value));
            default:
                return ((y7.m) this.f2489c).containsKey(obj);
        }
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
    public Iterator iterator() {
        switch (this.f2488b) {
            case 0:
                return new h1((c1) this.f2489c);
            default:
                return new y7.j((y7.m) this.f2489c);
        }
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public final boolean remove(Object obj) {
        switch (this.f2488b) {
            case 0:
                Map.Entry entry = (Map.Entry) obj;
                if (!contains(entry)) {
                    return false;
                }
                ((c1) this.f2489c).remove(entry.getKey());
                return true;
            default:
                y7.m mVar = (y7.m) this.f2489c;
                y7.l lVarA = null;
                if (obj != null) {
                    try {
                        lVarA = mVar.a(obj, false);
                        break;
                    } catch (ClassCastException unused) {
                    }
                }
                if (lVarA != null) {
                    mVar.c(lVarA, true);
                }
                return lVarA != null;
        }
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public final int size() {
        switch (this.f2488b) {
            case 0:
                return ((c1) this.f2489c).size();
            default:
                return ((y7.m) this.f2489c).f65135i;
        }
    }
}

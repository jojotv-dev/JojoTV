package androidx.datastore.preferences.protobuf;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/* JADX INFO: loaded from: classes.dex */
public final class c1 extends AbstractMap {

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public static final /* synthetic */ int f2434o = 0;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f2435b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public List f2436c = Collections.EMPTY_LIST;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public Map f2437e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public boolean f2438i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public volatile i1 f2439m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public Map f2440n;

    public c1(int i10) {
        this.f2435b = i10;
        Map map = Collections.EMPTY_MAP;
        this.f2437e = map;
        this.f2440n = map;
    }

    /* JADX WARN: Code duplicated, block: B:13:0x0026  */
    /* JADX WARN: Code duplicated, block: B:17:0x0042  */
    /* JADX WARN: Code duplicated, block: B:21:0x0040 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:22:0x0046 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:23:0x003c A[SYNTHETIC] */
    public final int a(Comparable comparable) {
        int i10;
        int i11;
        int i12;
        int iCompareTo;
        int size = this.f2436c.size();
        int i13 = size - 1;
        if (i13 < 0) {
            i10 = 0;
            while (i10 <= i13) {
                i12 = (i10 + i13) / 2;
                iCompareTo = comparable.compareTo(((g1) this.f2436c.get(i12)).a());
                if (iCompareTo < 0) {
                    i13 = i12 - 1;
                } else {
                    if (iCompareTo > 0) {
                        return i12;
                    }
                    i10 = i12 + 1;
                }
            }
            i11 = i10 + 1;
        } else {
            int iCompareTo2 = comparable.compareTo(((g1) this.f2436c.get(i13)).a());
            if (iCompareTo2 > 0) {
                i11 = size + 1;
            } else {
                if (iCompareTo2 == 0) {
                    return i13;
                }
                i10 = 0;
                while (i10 <= i13) {
                    i12 = (i10 + i13) / 2;
                    iCompareTo = comparable.compareTo(((g1) this.f2436c.get(i12)).a());
                    if (iCompareTo < 0) {
                        i13 = i12 - 1;
                    } else {
                        if (iCompareTo > 0) {
                            return i12;
                        }
                        i10 = i12 + 1;
                    }
                }
                i11 = i10 + 1;
            }
        }
        return -i11;
    }

    public final void b() {
        if (this.f2438i) {
            throw new UnsupportedOperationException();
        }
    }

    public final Map.Entry c(int i10) {
        return (Map.Entry) this.f2436c.get(i10);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final void clear() {
        b();
        if (!this.f2436c.isEmpty()) {
            this.f2436c.clear();
        }
        if (this.f2437e.isEmpty()) {
            return;
        }
        this.f2437e.clear();
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final boolean containsKey(Object obj) {
        Comparable comparable = (Comparable) obj;
        return a(comparable) >= 0 || this.f2437e.containsKey(comparable);
    }

    public final Iterable d() {
        return this.f2437e.isEmpty() ? f1.f2466b : this.f2437e.entrySet();
    }

    public final SortedMap e() {
        b();
        if (this.f2437e.isEmpty() && !(this.f2437e instanceof TreeMap)) {
            TreeMap treeMap = new TreeMap();
            this.f2437e = treeMap;
            this.f2440n = treeMap.descendingMap();
        }
        return (SortedMap) this.f2437e;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final Set entrySet() {
        if (this.f2439m == null) {
            this.f2439m = new i1(this, 0);
        }
        return this.f2439m;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof c1)) {
            return super.equals(obj);
        }
        c1 c1Var = (c1) obj;
        int size = size();
        if (size == c1Var.size()) {
            int size2 = this.f2436c.size();
            if (size2 != c1Var.f2436c.size()) {
                return ((AbstractSet) entrySet()).equals(c1Var.entrySet());
            }
            for (int i10 = 0; i10 < size2; i10++) {
                if (c(i10).equals(c1Var.c(i10))) {
                }
            }
            if (size2 != size) {
                return this.f2437e.equals(c1Var.f2437e);
            }
            return true;
        }
        return false;
    }

    @Override // java.util.AbstractMap, java.util.Map
    /* JADX INFO: renamed from: f, reason: merged with bridge method [inline-methods] */
    public final Object put(Comparable comparable, Object obj) {
        b();
        int iA = a(comparable);
        if (iA >= 0) {
            return ((g1) this.f2436c.get(iA)).setValue(obj);
        }
        b();
        boolean zIsEmpty = this.f2436c.isEmpty();
        int i10 = this.f2435b;
        if (zIsEmpty && !(this.f2436c instanceof ArrayList)) {
            this.f2436c = new ArrayList(i10);
        }
        int i11 = -(iA + 1);
        if (i11 >= i10) {
            return e().put(comparable, obj);
        }
        if (this.f2436c.size() == i10) {
            g1 g1Var = (g1) this.f2436c.remove(i10 - 1);
            e().put(g1Var.a(), g1Var.getValue());
        }
        this.f2436c.add(i11, new g1(this, comparable, obj));
        return null;
    }

    public final Object g(int i10) {
        b();
        Object value = ((g1) this.f2436c.remove(i10)).getValue();
        if (!this.f2437e.isEmpty()) {
            Iterator it = e().entrySet().iterator();
            this.f2436c.add(new g1(this, (Map.Entry) it.next()));
            it.remove();
        }
        return value;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final Object get(Object obj) {
        Comparable comparable = (Comparable) obj;
        int iA = a(comparable);
        return iA >= 0 ? ((g1) this.f2436c.get(iA)).getValue() : this.f2437e.get(comparable);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final int hashCode() {
        int size = this.f2436c.size();
        int iHashCode = 0;
        for (int i10 = 0; i10 < size; i10++) {
            iHashCode += ((g1) this.f2436c.get(i10)).hashCode();
        }
        return this.f2437e.size() > 0 ? this.f2437e.hashCode() + iHashCode : iHashCode;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final Object remove(Object obj) {
        b();
        Comparable comparable = (Comparable) obj;
        int iA = a(comparable);
        if (iA >= 0) {
            return g(iA);
        }
        if (this.f2437e.isEmpty()) {
            return null;
        }
        return this.f2437e.remove(comparable);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final int size() {
        return this.f2437e.size() + this.f2436c.size();
    }
}

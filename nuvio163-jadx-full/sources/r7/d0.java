package r7;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public final class d0 extends AbstractMap implements Serializable {

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    public static final Object f51220r = new Object();

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public transient Object f51221b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public transient int[] f51222c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public transient Object[] f51223e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public transient Object[] f51224i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public transient int f51225m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public transient int f51226n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public transient b0 f51227o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public transient b0 f51228p;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public transient s f51229q;

    public static d0 a() {
        d0 d0Var = new d0();
        d0Var.f51225m = l3.c.C(3, 1);
        return d0Var;
    }

    public static d0 b(int i10) {
        d0 d0Var = new d0();
        l3.c.u(i10 >= 0, "Expected size must be >= 0");
        d0Var.f51225m = l3.c.C(i10, 1);
        return d0Var;
    }

    public final Map c() {
        Object obj = this.f51221b;
        if (obj instanceof Map) {
            return (Map) obj;
        }
        return null;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final void clear() {
        if (g()) {
            return;
        }
        this.f51225m += 32;
        Map mapC = c();
        if (mapC != null) {
            this.f51225m = l3.c.C(size(), 3);
            mapC.clear();
            this.f51221b = null;
            this.f51226n = 0;
            return;
        }
        Arrays.fill(j(), 0, this.f51226n, (Object) null);
        Arrays.fill(k(), 0, this.f51226n, (Object) null);
        Object obj = this.f51221b;
        Objects.requireNonNull(obj);
        hh.a.c0(obj);
        Arrays.fill(i(), 0, this.f51226n, 0);
        this.f51226n = 0;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final boolean containsKey(Object obj) {
        Map mapC = c();
        if (mapC != null) {
            return mapC.containsKey(obj);
        }
        return e(obj) != -1;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final boolean containsValue(Object obj) {
        Map mapC = c();
        if (mapC != null) {
            return mapC.containsValue(obj);
        }
        for (int i10 = 0; i10 < this.f51226n; i10++) {
            if (c6.a.o(obj, k()[i10])) {
                return true;
            }
        }
        return false;
    }

    public final int d() {
        return (1 << (this.f51225m & 31)) - 1;
    }

    public final int e(Object obj) {
        if (g()) {
            return -1;
        }
        int iQ = z.q(obj);
        int iD = d();
        Object obj2 = this.f51221b;
        Objects.requireNonNull(obj2);
        int iD0 = hh.a.d0(iQ & iD, obj2);
        if (iD0 == 0) {
            return -1;
        }
        int iX = hh.a.x(iQ, iD);
        do {
            int i10 = iD0 - 1;
            int i11 = i()[i10];
            if (hh.a.x(i11, iD) == iX && c6.a.o(obj, j()[i10])) {
                return i10;
            }
            iD0 = hh.a.y(i11, iD);
        } while (iD0 != 0);
        return -1;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final Set entrySet() {
        b0 b0Var = this.f51228p;
        if (b0Var != null) {
            return b0Var;
        }
        b0 b0Var2 = new b0(this, 0);
        this.f51228p = b0Var2;
        return b0Var2;
    }

    public final void f(int i10, int i11) {
        Object obj = this.f51221b;
        Objects.requireNonNull(obj);
        int[] iArrI = i();
        Object[] objArrJ = j();
        Object[] objArrK = k();
        int size = size();
        int i12 = size - 1;
        if (i10 >= i12) {
            objArrJ[i10] = null;
            objArrK[i10] = null;
            iArrI[i10] = 0;
            return;
        }
        Object obj2 = objArrJ[i12];
        objArrJ[i10] = obj2;
        objArrK[i10] = objArrK[i12];
        objArrJ[i12] = null;
        objArrK[i12] = null;
        iArrI[i10] = iArrI[i12];
        iArrI[i12] = 0;
        int iQ = z.q(obj2) & i11;
        int iD0 = hh.a.d0(iQ, obj);
        if (iD0 == size) {
            hh.a.e0(iQ, i10 + 1, obj);
            return;
        }
        while (true) {
            int i13 = iD0 - 1;
            int i14 = iArrI[i13];
            int iY = hh.a.y(i14, i11);
            if (iY == size) {
                iArrI[i13] = hh.a.J(i14, i10 + 1, i11);
                return;
            }
            iD0 = iY;
        }
    }

    public final boolean g() {
        return this.f51221b == null;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final Object get(Object obj) {
        Map mapC = c();
        if (mapC != null) {
            return mapC.get(obj);
        }
        int iE = e(obj);
        if (iE == -1) {
            return null;
        }
        return k()[iE];
    }

    public final Object h(Object obj) {
        if (!g()) {
            int iD = d();
            Object obj2 = this.f51221b;
            Objects.requireNonNull(obj2);
            int iV = hh.a.V(obj, null, iD, obj2, i(), j(), null);
            if (iV != -1) {
                Object obj3 = k()[iV];
                f(iV, iD);
                this.f51226n--;
                this.f51225m += 32;
                return obj3;
            }
        }
        return f51220r;
    }

    public final int[] i() {
        int[] iArr = this.f51222c;
        Objects.requireNonNull(iArr);
        return iArr;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final boolean isEmpty() {
        return size() == 0;
    }

    public final Object[] j() {
        Object[] objArr = this.f51223e;
        Objects.requireNonNull(objArr);
        return objArr;
    }

    public final Object[] k() {
        Object[] objArr = this.f51224i;
        Objects.requireNonNull(objArr);
        return objArr;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final Set keySet() {
        b0 b0Var = this.f51227o;
        if (b0Var != null) {
            return b0Var;
        }
        b0 b0Var2 = new b0(this, 1);
        this.f51227o = b0Var2;
        return b0Var2;
    }

    public final int l(int i10, int i11, int i12, int i13) {
        Object objP = hh.a.p(i11);
        int i14 = i11 - 1;
        if (i13 != 0) {
            hh.a.e0(i12 & i14, i13 + 1, objP);
        }
        Object obj = this.f51221b;
        Objects.requireNonNull(obj);
        int[] iArrI = i();
        for (int i15 = 0; i15 <= i10; i15++) {
            int iD0 = hh.a.d0(i15, obj);
            while (iD0 != 0) {
                int i16 = iD0 - 1;
                int i17 = iArrI[i16];
                int iX = hh.a.x(i17, i10) | i15;
                int i18 = iX & i14;
                int iD1 = hh.a.d0(i18, objP);
                hh.a.e0(i18, iD0, objP);
                iArrI[i16] = hh.a.J(iX, iD1, i14);
                iD0 = hh.a.y(i17, i10);
            }
        }
        this.f51221b = objP;
        this.f51225m = hh.a.J(this.f51225m, 32 - Integer.numberOfLeadingZeros(i14), 31);
        return i14;
    }

    /* JADX WARN: Code duplicated, block: B:31:0x00c9  */
    /* JADX WARN: Code duplicated, block: B:50:0x00df A[SYNTHETIC] */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:32:0x00dc -> B:28:0x00c4). Please report as a decompilation issue!!! */
    /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: Regions count limit reached at block B:26:0x00b0
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
        	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
        	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
        */
    @Override // java.util.AbstractMap, java.util.Map
    public final java.lang.Object put(java.lang.Object r18, java.lang.Object r19) {
        /*
            Method dump skipped, instruction units count: 352
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: r7.d0.put(java.lang.Object, java.lang.Object):java.lang.Object");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final Object remove(Object obj) {
        Map mapC = c();
        if (mapC != null) {
            return mapC.remove(obj);
        }
        Object objH = h(obj);
        if (objH == f51220r) {
            return null;
        }
        return objH;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final int size() {
        Map mapC = c();
        return mapC != null ? mapC.size() : this.f51226n;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final Collection values() {
        s sVar = this.f51229q;
        if (sVar != null) {
            return sVar;
        }
        s sVar2 = new s(1, this);
        this.f51229q = sVar2;
        return sVar2;
    }
}

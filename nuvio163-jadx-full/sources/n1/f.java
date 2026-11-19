package n1;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.TypeIntrinsics;
import kotlin.jvm.internal.markers.KMutableIterator;

/* JADX INFO: loaded from: classes.dex */
public class f extends d implements KMutableIterator {

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final e f39432i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public Object f39433m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public boolean f39434n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public int f39435o;

    public f(e eVar, o[] oVarArr) {
        super(eVar.f39428e, oVarArr);
        this.f39432i = eVar;
        this.f39435o = eVar.f39430m;
    }

    public final void c(int i10, n nVar, Object obj, int i11) {
        int i12 = i11 * 5;
        o[] oVarArr = this.f39423b;
        if (i12 <= 30) {
            int iP = 1 << l3.c.P(i10, i12);
            if (nVar.h(iP)) {
                oVarArr[i11].a(Integer.bitCount(nVar.f39444a) * 2, nVar.f(iP), nVar.f39447d);
                this.f39424c = i11;
                return;
            }
            int iT = nVar.t(iP);
            n nVarS = nVar.s(iT);
            oVarArr[i11].a(Integer.bitCount(nVar.f39444a) * 2, iT, nVar.f39447d);
            c(i10, nVarS, obj, i11 + 1);
            return;
        }
        o oVar = oVarArr[i11];
        Object[] objArr = nVar.f39447d;
        oVar.a(objArr.length, 0, objArr);
        while (true) {
            o oVar2 = oVarArr[i11];
            if (Intrinsics.areEqual(oVar2.f39448b[oVar2.f39450e], obj)) {
                this.f39424c = i11;
                return;
            } else {
                oVarArr[i11].f39450e += 2;
            }
        }
    }

    @Override // n1.d, java.util.Iterator
    public final Object next() {
        if (this.f39432i.f39430m != this.f39435o) {
            throw new ConcurrentModificationException();
        }
        if (!this.f39425e) {
            throw new NoSuchElementException();
        }
        o oVar = this.f39423b[this.f39424c];
        this.f39433m = oVar.f39448b[oVar.f39450e];
        this.f39434n = true;
        return super.next();
    }

    @Override // n1.d, java.util.Iterator
    public final void remove() {
        if (!this.f39434n) {
            throw new IllegalStateException();
        }
        boolean z2 = this.f39425e;
        e eVar = this.f39432i;
        if (!z2) {
            TypeIntrinsics.asMutableMap(eVar).remove(this.f39433m);
        } else {
            if (!z2) {
                throw new NoSuchElementException();
            }
            o oVar = this.f39423b[this.f39424c];
            Object obj = oVar.f39448b[oVar.f39450e];
            TypeIntrinsics.asMutableMap(eVar).remove(this.f39433m);
            c(obj != null ? obj.hashCode() : 0, eVar.f39428e, obj, 0);
        }
        this.f39433m = null;
        this.f39434n = false;
        this.f39435o = eVar.f39430m;
    }
}

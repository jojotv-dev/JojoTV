package n1;

import java.util.Iterator;
import java.util.NoSuchElementException;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.markers.KMappedMarker;

/* JADX INFO: loaded from: classes.dex */
public abstract class d implements Iterator, KMappedMarker {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final o[] f39423b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f39424c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public boolean f39425e = true;

    public d(n nVar, o[] oVarArr) {
        this.f39423b = oVarArr;
        oVarArr[0].a(Integer.bitCount(nVar.f39444a) * 2, 0, nVar.f39447d);
        this.f39424c = 0;
        a();
    }

    public final void a() {
        int i10 = this.f39424c;
        o[] oVarArr = this.f39423b;
        o oVar = oVarArr[i10];
        if (oVar.f39450e < oVar.f39449c) {
            return;
        }
        while (-1 < i10) {
            int iB = b(i10);
            if (iB == -1) {
                o oVar2 = oVarArr[i10];
                int i11 = oVar2.f39450e;
                Object[] objArr = oVar2.f39448b;
                if (i11 < objArr.length) {
                    int length = objArr.length;
                    oVar2.f39450e = i11 + 1;
                    iB = b(i10);
                }
            }
            if (iB != -1) {
                this.f39424c = iB;
                return;
            }
            if (i10 > 0) {
                o oVar3 = oVarArr[i10 - 1];
                int i12 = oVar3.f39450e;
                int length2 = oVar3.f39448b.length;
                oVar3.f39450e = i12 + 1;
            }
            oVarArr[i10].a(0, 0, n.f39443e.f39447d);
            i10--;
        }
        this.f39425e = false;
    }

    public final int b(int i10) {
        o[] oVarArr = this.f39423b;
        o oVar = oVarArr[i10];
        int i11 = oVar.f39450e;
        if (i11 < oVar.f39449c) {
            return i10;
        }
        Object[] objArr = oVar.f39448b;
        if (i11 >= objArr.length) {
            return -1;
        }
        int length = objArr.length;
        Object obj = objArr[i11];
        Intrinsics.checkNotNull(obj, "null cannot be cast to non-null type androidx.compose.runtime.external.kotlinx.collections.immutable.implementations.immutableMap.TrieNode<K of androidx.compose.runtime.external.kotlinx.collections.immutable.implementations.immutableMap.TrieNodeBaseIterator, V of androidx.compose.runtime.external.kotlinx.collections.immutable.implementations.immutableMap.TrieNodeBaseIterator>");
        n nVar = (n) obj;
        if (i10 == 6) {
            o oVar2 = oVarArr[i10 + 1];
            Object[] objArr2 = nVar.f39447d;
            oVar2.a(objArr2.length, 0, objArr2);
        } else {
            oVarArr[i10 + 1].a(Integer.bitCount(nVar.f39444a) * 2, 0, nVar.f39447d);
        }
        return b(i10 + 1);
    }

    @Override // java.util.Iterator
    public final boolean hasNext() {
        return this.f39425e;
    }

    @Override // java.util.Iterator
    public Object next() {
        if (!this.f39425e) {
            throw new NoSuchElementException();
        }
        Object next = this.f39423b[this.f39424c].next();
        a();
        return next;
    }

    @Override // java.util.Iterator
    public void remove() {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
}

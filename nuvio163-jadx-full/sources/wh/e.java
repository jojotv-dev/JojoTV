package wh;

import java.util.Iterator;
import java.util.NoSuchElementException;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.markers.KMappedMarker;

/* JADX INFO: loaded from: classes5.dex */
public abstract class e implements Iterator, KMappedMarker {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final p[] f62548b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f62549c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public boolean f62550e;

    public e(o node, p[] path) {
        Intrinsics.checkNotNullParameter(node, "node");
        Intrinsics.checkNotNullParameter(path, "path");
        this.f62548b = path;
        this.f62550e = true;
        p pVar = path[0];
        Object[] buffer = node.f62575d;
        int iBitCount = Integer.bitCount(node.f62572a) * 2;
        pVar.getClass();
        Intrinsics.checkNotNullParameter(buffer, "buffer");
        Intrinsics.checkNotNullParameter(buffer, "buffer");
        pVar.f62576b = buffer;
        pVar.f62577c = iBitCount;
        pVar.f62578e = 0;
        this.f62549c = 0;
        a();
    }

    public final void a() {
        int i10 = this.f62549c;
        p[] pVarArr = this.f62548b;
        p pVar = pVarArr[i10];
        if (pVar.f62578e < pVar.f62577c) {
            return;
        }
        while (-1 < i10) {
            int iB = b(i10);
            if (iB == -1) {
                p pVar2 = pVarArr[i10];
                int i11 = pVar2.f62578e;
                Object[] objArr = pVar2.f62576b;
                if (i11 < objArr.length) {
                    int length = objArr.length;
                    pVar2.f62578e = i11 + 1;
                    iB = b(i10);
                }
            }
            if (iB != -1) {
                this.f62549c = iB;
                return;
            }
            if (i10 > 0) {
                p pVar3 = pVarArr[i10 - 1];
                int i12 = pVar3.f62578e;
                int length2 = pVar3.f62576b.length;
                pVar3.f62578e = i12 + 1;
            }
            p pVar4 = pVarArr[i10];
            Object[] buffer = o.f62571e.f62575d;
            pVar4.getClass();
            Intrinsics.checkNotNullParameter(buffer, "buffer");
            Intrinsics.checkNotNullParameter(buffer, "buffer");
            pVar4.f62576b = buffer;
            pVar4.f62577c = 0;
            pVar4.f62578e = 0;
            i10--;
        }
        this.f62550e = false;
    }

    public final int b(int i10) {
        p[] pVarArr = this.f62548b;
        p pVar = pVarArr[i10];
        int i11 = pVar.f62578e;
        if (i11 < pVar.f62577c) {
            return i10;
        }
        Object[] objArr = pVar.f62576b;
        if (i11 >= objArr.length) {
            return -1;
        }
        int length = objArr.length;
        Object obj = objArr[i11];
        Intrinsics.checkNotNull(obj, "null cannot be cast to non-null type kotlinx.collections.immutable.implementations.immutableMap.TrieNode<K of kotlinx.collections.immutable.implementations.immutableMap.TrieNodeBaseIterator, V of kotlinx.collections.immutable.implementations.immutableMap.TrieNodeBaseIterator>");
        o oVar = (o) obj;
        if (i10 == 6) {
            p pVar2 = pVarArr[i10 + 1];
            Object[] buffer = oVar.f62575d;
            int length2 = buffer.length;
            pVar2.getClass();
            Intrinsics.checkNotNullParameter(buffer, "buffer");
            Intrinsics.checkNotNullParameter(buffer, "buffer");
            pVar2.f62576b = buffer;
            pVar2.f62577c = length2;
            pVar2.f62578e = 0;
        } else {
            p pVar3 = pVarArr[i10 + 1];
            Object[] buffer2 = oVar.f62575d;
            int iBitCount = Integer.bitCount(oVar.f62572a) * 2;
            pVar3.getClass();
            Intrinsics.checkNotNullParameter(buffer2, "buffer");
            Intrinsics.checkNotNullParameter(buffer2, "buffer");
            pVar3.f62576b = buffer2;
            pVar3.f62577c = iBitCount;
            pVar3.f62578e = 0;
        }
        return b(i10 + 1);
    }

    @Override // java.util.Iterator
    public final boolean hasNext() {
        return this.f62550e;
    }

    @Override // java.util.Iterator
    public Object next() {
        if (!this.f62550e) {
            throw new NoSuchElementException();
        }
        Object next = this.f62548b[this.f62549c].next();
        a();
        return next;
    }

    @Override // java.util.Iterator
    public void remove() {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
}

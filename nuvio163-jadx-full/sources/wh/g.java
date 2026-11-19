package wh;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.TypeIntrinsics;
import kotlin.jvm.internal.markers.KMutableIterator;

/* JADX INFO: loaded from: classes5.dex */
public class g extends e implements KMutableIterator {

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final f f62557i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public Object f62558m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public boolean f62559n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public int f62560o;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public g(f builder, p[] path) {
        super(builder.f62553e, path);
        Intrinsics.checkNotNullParameter(builder, "builder");
        Intrinsics.checkNotNullParameter(path, "path");
        this.f62557i = builder;
        this.f62560o = builder.f62555m;
    }

    public final void c(int i10, o oVar, Object obj, int i11) {
        int i12 = i11 * 5;
        p[] pVarArr = this.f62548b;
        if (i12 <= 30) {
            int iN = 1 << r.n(i10, i12);
            if (oVar.i(iN)) {
                int iF = oVar.f(iN);
                p pVar = pVarArr[i11];
                Object[] buffer = oVar.f62575d;
                int iBitCount = Integer.bitCount(oVar.f62572a) * 2;
                pVar.getClass();
                Intrinsics.checkNotNullParameter(buffer, "buffer");
                pVar.f62576b = buffer;
                pVar.f62577c = iBitCount;
                pVar.f62578e = iF;
                this.f62549c = i11;
                return;
            }
            int iU = oVar.u(iN);
            o oVarT = oVar.t(iU);
            p pVar2 = pVarArr[i11];
            Object[] buffer2 = oVar.f62575d;
            int iBitCount2 = Integer.bitCount(oVar.f62572a) * 2;
            pVar2.getClass();
            Intrinsics.checkNotNullParameter(buffer2, "buffer");
            pVar2.f62576b = buffer2;
            pVar2.f62577c = iBitCount2;
            pVar2.f62578e = iU;
            c(i10, oVarT, obj, i11 + 1);
            return;
        }
        p pVar3 = pVarArr[i11];
        Object[] buffer3 = oVar.f62575d;
        int length = buffer3.length;
        pVar3.getClass();
        Intrinsics.checkNotNullParameter(buffer3, "buffer");
        pVar3.f62576b = buffer3;
        pVar3.f62577c = length;
        pVar3.f62578e = 0;
        while (true) {
            p pVar4 = pVarArr[i11];
            if (Intrinsics.areEqual(pVar4.f62576b[pVar4.f62578e], obj)) {
                this.f62549c = i11;
                return;
            } else {
                pVarArr[i11].f62578e += 2;
            }
        }
    }

    @Override // wh.e, java.util.Iterator
    public final Object next() {
        if (this.f62557i.f62555m != this.f62560o) {
            throw new ConcurrentModificationException();
        }
        if (!this.f62550e) {
            throw new NoSuchElementException();
        }
        p pVar = this.f62548b[this.f62549c];
        this.f62558m = pVar.f62576b[pVar.f62578e];
        this.f62559n = true;
        return super.next();
    }

    @Override // wh.e, java.util.Iterator
    public final void remove() {
        if (!this.f62559n) {
            throw new IllegalStateException();
        }
        boolean z2 = this.f62550e;
        f fVar = this.f62557i;
        if (!z2) {
            TypeIntrinsics.asMutableMap(fVar).remove(this.f62558m);
        } else {
            if (!z2) {
                throw new NoSuchElementException();
            }
            p pVar = this.f62548b[this.f62549c];
            Object obj = pVar.f62576b[pVar.f62578e];
            TypeIntrinsics.asMutableMap(fVar).remove(this.f62558m);
            c(obj != null ? obj.hashCode() : 0, fVar.f62553e, obj, 0);
        }
        this.f62558m = null;
        this.f62559n = false;
        this.f62560o = fVar.f62555m;
    }
}

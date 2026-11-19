package il;

import java.math.BigInteger;

/* JADX INFO: loaded from: classes5.dex */
public final class d implements a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final a f24223a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final c f24224b;

    public d(a aVar, c cVar) {
        this.f24223a = aVar;
        this.f24224b = cVar;
    }

    @Override // il.a
    public final int a() {
        int iA = this.f24223a.a();
        int[] iArr = this.f24224b.f24222a;
        return iA * iArr[iArr.length - 1];
    }

    @Override // il.a
    public final BigInteger b() {
        return this.f24223a.b();
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof d)) {
            return false;
        }
        d dVar = (d) obj;
        return this.f24223a.equals(dVar.f24223a) && this.f24224b.equals(dVar.f24224b);
    }

    public final int hashCode() {
        return this.f24223a.hashCode() ^ Integer.rotateLeft(this.f24224b.hashCode(), 16);
    }
}

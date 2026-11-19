package nk;

import java.math.BigInteger;

/* JADX INFO: loaded from: classes5.dex */
public final class d implements ek.d {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public BigInteger f42891b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public BigInteger f42892c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public BigInteger f42893e;

    public final boolean equals(Object obj) {
        if (!(obj instanceof d)) {
            return false;
        }
        d dVar = (d) obj;
        BigInteger bigInteger = dVar.f42893e;
        BigInteger bigInteger2 = this.f42893e;
        if (bigInteger2 != null) {
            if (!bigInteger2.equals(bigInteger)) {
                return false;
            }
        } else if (bigInteger != null) {
            return false;
        }
        return dVar.f42892c.equals(this.f42892c) && dVar.f42891b.equals(this.f42891b);
    }

    public final int hashCode() {
        int iHashCode = this.f42892c.hashCode() ^ this.f42891b.hashCode();
        BigInteger bigInteger = this.f42893e;
        return iHashCode ^ (bigInteger != null ? bigInteger.hashCode() : 0);
    }
}

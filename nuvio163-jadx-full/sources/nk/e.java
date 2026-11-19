package nk;

import java.math.BigInteger;

/* JADX INFO: loaded from: classes5.dex */
public final class e implements ek.d {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final BigInteger f42894b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final BigInteger f42895c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final BigInteger f42896e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final f f42897i;

    public e(BigInteger bigInteger, BigInteger bigInteger2, BigInteger bigInteger3, f fVar) {
        this.f42894b = bigInteger3;
        this.f42896e = bigInteger;
        this.f42895c = bigInteger2;
        this.f42897i = fVar;
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof e)) {
            return false;
        }
        e eVar = (e) obj;
        return eVar.f42896e.equals(this.f42896e) && eVar.f42895c.equals(this.f42895c) && eVar.f42894b.equals(this.f42894b);
    }

    public final int hashCode() {
        return (this.f42896e.hashCode() ^ this.f42895c.hashCode()) ^ this.f42894b.hashCode();
    }
}

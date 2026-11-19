package il;

import java.math.BigInteger;

/* JADX INFO: loaded from: classes5.dex */
public final class e implements a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final BigInteger f24225a;

    public e(BigInteger bigInteger) {
        this.f24225a = bigInteger;
    }

    @Override // il.a
    public final int a() {
        return 1;
    }

    @Override // il.a
    public final BigInteger b() {
        return this.f24225a;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof e) {
            return this.f24225a.equals(((e) obj).f24225a);
        }
        return false;
    }

    public final int hashCode() {
        return this.f24225a.hashCode();
    }
}

package i9;

import java.util.Objects;

/* JADX INFO: loaded from: classes4.dex */
public final class k {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public i f23888a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public j f23889b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f23890c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public h f23891d;

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && k.class == obj.getClass()) {
            k kVar = (k) obj;
            if (this.f23890c == kVar.f23890c && this.f23888a == kVar.f23888a && this.f23889b == kVar.f23889b && this.f23891d == kVar.f23891d) {
                return true;
            }
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(this.f23888a, this.f23889b, Integer.valueOf(this.f23890c), this.f23891d);
    }

    public final String toString() {
        return String.format("WindowsVersion[%s, %s, %d, %s]", this.f23888a, this.f23889b, Integer.valueOf(this.f23890c), this.f23891d);
    }
}

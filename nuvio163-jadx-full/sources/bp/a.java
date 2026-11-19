package bp;

import java.io.Serializable;

/* JADX INFO: loaded from: classes5.dex */
public final class a implements Serializable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f6294b;

    public a(String str) {
        this.f6294b = str;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof a) {
            return this.f6294b.equals(((a) obj).f6294b);
        }
        return false;
    }

    public final int hashCode() {
        return this.f6294b.hashCode();
    }

    public final String toString() {
        return this.f6294b;
    }
}

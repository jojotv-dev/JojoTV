package j4;

import android.os.Parcelable;
import java.util.Objects;

/* JADX INFO: loaded from: classes3.dex */
public final class b {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Object f27185a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Object f27186b;

    public b(Object obj, Parcelable parcelable) {
        this.f27185a = obj;
        this.f27186b = parcelable;
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof b)) {
            return false;
        }
        b bVar = (b) obj;
        return Objects.equals(bVar.f27185a, this.f27185a) && Objects.equals(bVar.f27186b, this.f27186b);
    }

    public final int hashCode() {
        Object obj = this.f27185a;
        int iHashCode = obj == null ? 0 : obj.hashCode();
        Object obj2 = this.f27186b;
        return (obj2 != null ? obj2.hashCode() : 0) ^ iHashCode;
    }

    public final String toString() {
        return "Pair{" + this.f27185a + " " + this.f27186b + "}";
    }
}

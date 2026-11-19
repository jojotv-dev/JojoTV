package g4;

import java.util.List;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public final class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public String f18771a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public String f18772b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public List f18773c;

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof a)) {
            return false;
        }
        a aVar = (a) obj;
        return Objects.equals(this.f18771a, aVar.f18771a) && Objects.equals(this.f18772b, aVar.f18772b) && Objects.equals(this.f18773c, aVar.f18773c);
    }

    public final int hashCode() {
        return Objects.hash(this.f18771a, this.f18772b, this.f18773c);
    }
}

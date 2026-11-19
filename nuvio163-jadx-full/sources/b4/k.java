package b4;

import android.content.res.Resources;
import java.util.Objects;

/* JADX INFO: loaded from: classes3.dex */
public final class k {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Resources f4329a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Resources.Theme f4330b;

    public k(Resources resources, Resources.Theme theme) {
        this.f4329a = resources;
        this.f4330b = theme;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && k.class == obj.getClass()) {
            k kVar = (k) obj;
            if (this.f4329a.equals(kVar.f4329a) && Objects.equals(this.f4330b, kVar.f4330b)) {
                return true;
            }
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(this.f4329a, this.f4330b);
    }
}

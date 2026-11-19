package f4;

import android.os.LocaleList;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public final class g {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final g f16186b = new g(new h(new LocaleList(new Locale[0])));

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final h f16187a;

    public g(h hVar) {
        this.f16187a = hVar;
    }

    public final boolean equals(Object obj) {
        if (obj instanceof g) {
            return this.f16187a.equals(((g) obj).f16187a);
        }
        return false;
    }

    public final int hashCode() {
        return this.f16187a.f16188a.hashCode();
    }

    public final String toString() {
        return this.f16187a.f16188a.toString();
    }
}

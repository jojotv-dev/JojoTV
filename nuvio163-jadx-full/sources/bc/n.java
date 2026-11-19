package bc;

import java.util.LinkedHashMap;
import java.util.Locale;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class n {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final LinkedHashMap f4439a = new LinkedHashMap();

    public final String a(String label) {
        Intrinsics.checkNotNullParameter(label, "label");
        String lowerCase = label.toLowerCase(Locale.ROOT);
        Intrinsics.checkNotNullExpressionValue(lowerCase, "toLowerCase(...)");
        String str = (String) this.f4439a.get(lowerCase);
        return str == null ? "" : str;
    }

    public final void b(String label, String str) {
        Intrinsics.checkNotNullParameter(label, "label");
        String lowerCase = label.toLowerCase(Locale.ROOT);
        Intrinsics.checkNotNullExpressionValue(lowerCase, "toLowerCase(...)");
        this.f4439a.put(lowerCase, str);
    }
}

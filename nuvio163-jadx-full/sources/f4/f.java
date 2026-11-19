package f4;

import java.util.Locale;

/* JADX INFO: loaded from: classes3.dex */
public abstract class f {
    static {
        new Locale("en", "XA");
        new Locale("ar", "XB");
    }

    public static Locale a(String str) {
        return Locale.forLanguageTag(str);
    }
}

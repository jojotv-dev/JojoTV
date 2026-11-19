package qj;

import java.util.HashMap;
import java.util.Locale;

/* JADX INFO: loaded from: classes5.dex */
public abstract class w1 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final Locale f50915a;

    static {
        Locale locale;
        new HashMap();
        if ("en".equalsIgnoreCase(Locale.getDefault().getLanguage())) {
            locale = Locale.getDefault();
        } else {
            Locale[] availableLocales = Locale.getAvailableLocales();
            for (int i10 = 0; i10 != availableLocales.length; i10++) {
                if ("en".equalsIgnoreCase(availableLocales[i10].getLanguage())) {
                    locale = availableLocales[i10];
                }
            }
            locale = Locale.getDefault();
        }
        f50915a = locale;
    }
}

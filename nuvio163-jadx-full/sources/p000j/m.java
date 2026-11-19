package p000j;

import android.content.res.Configuration;
import android.os.LocaleList;
import f4.f;
import f4.g;
import f4.h;
import java.util.Locale;

/* JADX INFO: loaded from: classes3.dex */
public abstract class m {
    public static void a(Configuration configuration, Configuration configuration2, Configuration configuration3) {
        LocaleList locales = configuration.getLocales();
        LocaleList locales2 = configuration2.getLocales();
        if (locales.equals(locales2)) {
            return;
        }
        configuration3.setLocales(locales2);
        configuration3.locale = configuration2.locale;
    }

    public static g b(Configuration configuration) {
        String languageTags = configuration.getLocales().toLanguageTags();
        if (languageTags != null) {
            g gVar = g.f16186b;
            if (!languageTags.isEmpty()) {
                String[] strArrSplit = languageTags.split(",", -1);
                int length = strArrSplit.length;
                Locale[] localeArr = new Locale[length];
                for (int i10 = 0; i10 < length; i10++) {
                    localeArr[i10] = f.a(strArrSplit[i10]);
                }
                return new g(new h(new LocaleList(localeArr)));
            }
        }
        return g.f16186b;
    }

    public static void c(g gVar) {
        LocaleList.setDefault(LocaleList.forLanguageTags(gVar.f16187a.f16188a.toLanguageTags()));
    }

    public static void d(Configuration configuration, g gVar) {
        configuration.setLocales(LocaleList.forLanguageTags(gVar.f16187a.f16188a.toLanguageTags()));
    }
}

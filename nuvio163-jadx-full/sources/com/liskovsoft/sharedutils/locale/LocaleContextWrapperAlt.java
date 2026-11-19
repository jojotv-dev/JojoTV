package com.liskovsoft.sharedutils.locale;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.os.Build;
import java.util.Locale;

/* JADX INFO: loaded from: classes4.dex */
public class LocaleContextWrapperAlt extends ContextWrapper {
    public LocaleContextWrapperAlt(Context context) {
        super(context);
    }

    @TargetApi(24)
    public static Locale getSystemLocale(Configuration configuration) {
        return configuration.getLocales().get(0);
    }

    public static Locale getSystemLocaleLegacy(Configuration configuration) {
        return configuration.locale;
    }

    @TargetApi(24)
    public static void setSystemLocale(Configuration configuration, Locale locale) {
        configuration.setLocale(locale);
    }

    public static void setSystemLocaleLegacy(Configuration configuration, Locale locale) {
        configuration.locale = locale;
    }

    public static ContextWrapper wrap(Context context, String str) {
        Configuration configuration = context.getResources().getConfiguration();
        Locale systemLocale = Build.VERSION.SDK_INT > 24 ? getSystemLocale(configuration) : getSystemLocaleLegacy(configuration);
        if (!str.equals("") && !systemLocale.getLanguage().equals(str)) {
            Locale locale = new Locale(str);
            Locale.setDefault(locale);
            setSystemLocale(configuration, locale);
        }
        return new LocaleContextWrapper(context.createConfigurationContext(configuration));
    }
}

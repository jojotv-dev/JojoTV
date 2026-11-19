package com.liskovsoft.sharedutils.locale;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.LocaleList;
import android.util.DisplayMetrics;
import java.util.Locale;

/* JADX INFO: loaded from: classes4.dex */
public class LocaleContextWrapper extends ContextWrapper {
    public LocaleContextWrapper(Context context) {
        super(context);
    }

    public static void applySavedLocale(Context context, Locale locale, DisplayMetrics displayMetrics) {
        Resources resources;
        if (context == null || locale == null || (resources = context.getResources()) == null) {
            return;
        }
        Configuration configuration = resources.getConfiguration();
        if (displayMetrics != null) {
            configuration.densityDpi = (int) (displayMetrics.density * 160.0f);
        }
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

    public static Context wrap(Context context, Locale locale) {
        return wrap(context, locale, null);
    }

    public static Context wrap(Context context, Locale locale, DisplayMetrics displayMetrics) {
        Resources resources;
        if (locale == null || (resources = context.getResources()) == null) {
            return context;
        }
        Configuration configuration = resources.getConfiguration();
        if (displayMetrics != null) {
            configuration.densityDpi = (int) (displayMetrics.density * 160.0f);
        }
        configuration.setLocale(locale);
        LocaleList localeList = new LocaleList(locale);
        LocaleList.setDefault(localeList);
        configuration.setLocales(localeList);
        return new ContextWrapper(context.createConfigurationContext(configuration));
    }
}

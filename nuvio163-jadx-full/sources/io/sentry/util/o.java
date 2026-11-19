package io.sentry.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes.dex */
public abstract class o {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final Charset f26269a = StandardCharsets.UTF_8;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final Pattern f26270b = Pattern.compile("[\\W_]+");

    public static String a(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        StringBuilder sb2 = new StringBuilder();
        String strSubstring = str.substring(0, 1);
        Locale locale = Locale.ROOT;
        sb2.append(strSubstring.toUpperCase(locale));
        sb2.append(str.substring(1).toLowerCase(locale));
        return sb2.toString();
    }

    public static String b(List list) {
        StringBuilder sb2 = new StringBuilder();
        Iterator it = list.iterator();
        if (it.hasNext()) {
            sb2.append((CharSequence) it.next());
            while (it.hasNext()) {
                sb2.append((CharSequence) ",");
                sb2.append((CharSequence) it.next());
            }
        }
        return sb2.toString();
    }

    public static String c(String str) {
        return (str != null && str.startsWith("\"") && str.endsWith("\"")) ? a4.b.f(1, 1, str) : str;
    }
}

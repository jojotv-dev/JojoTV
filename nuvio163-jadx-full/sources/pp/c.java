package pp;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import w.h0;

/* JADX INFO: loaded from: classes5.dex */
public abstract class c {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final Pattern f45262a = Pattern.compile("(https?)?://m\\.");

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final Pattern f45263b = Pattern.compile("(https?)?://www\\.");

    public static void a(String str, Pattern pattern) throws zo.c {
        if (g(str)) {
            throw new IllegalArgumentException("Url can't be null or empty");
        }
        if (!pattern.matcher(str.toLowerCase()).find()) {
            throw new zo.c("Url doesn't match the pattern");
        }
    }

    public static String b(String str) {
        return URLEncoder.encode(str, StandardCharsets.UTF_8);
    }

    public static String c(String str) {
        try {
            URL urlI = i(str);
            return (urlI.getHost().contains("google") && urlI.getPath().equals("/url")) ? URLDecoder.decode(hh.a.K(1, "&url=([^&]+)(?:&|$)", str), StandardCharsets.UTF_8) : str;
        } catch (Exception unused) {
            return str;
        }
    }

    public static String d(String str) throws zo.c {
        try {
            URL urlI = i(str);
            return urlI.getProtocol() + "://" + urlI.getAuthority();
        } catch (MalformedURLException e10) {
            String message = e10.getMessage();
            if (message.startsWith("unknown protocol: ")) {
                return message.substring(18);
            }
            throw new zo.c(h0.k("Malformed url: ", str), e10);
        }
    }

    public static String e(URL url, String str) {
        String query = url.getQuery();
        if (query == null) {
            return null;
        }
        for (String str2 : query.split("&")) {
            String[] strArrSplit = str2.split("=", 2);
            String str3 = strArrSplit[0];
            Charset charset = StandardCharsets.UTF_8;
            if (URLDecoder.decode(str3, charset).equals(str)) {
                return URLDecoder.decode(strArrSplit[1], charset);
            }
        }
        return null;
    }

    public static boolean f(URL url) {
        String protocol = url.getProtocol();
        if (protocol.equals("http") || protocol.equals("https")) {
            boolean z2 = url.getPort() == url.getDefaultPort();
            if (url.getPort() == -1 || z2) {
                return true;
            }
        }
        return false;
    }

    public static boolean g(String str) {
        return str == null || str.isEmpty();
    }

    public static String h(String str) {
        if (str == null) {
            return null;
        }
        return str.startsWith("http://") ? h0.k("https://", str.substring(7)) : str;
    }

    public static URL i(String str) throws MalformedURLException {
        try {
            return new URL(str);
        } catch (MalformedURLException e10) {
            if (e10.getMessage().equals("no protocol: " + str)) {
                return new URL(h0.k("https://", str));
            }
            throw e10;
        }
    }
}

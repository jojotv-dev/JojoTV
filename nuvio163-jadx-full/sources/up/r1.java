package up;

import java.util.HashMap;
import java.util.StringTokenizer;

/* JADX INFO: loaded from: classes5.dex */
public abstract class r1 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static HashMap f60251a;

    static {
        try {
            b();
        } catch (SecurityException unused) {
        }
    }

    public static boolean a(String str) {
        HashMap map = f60251a;
        return (map == null || map.get(str.toLowerCase()) == null) ? false : true;
    }

    public static void b() {
        String property = System.getProperty("dnsjava.options");
        if (property != null) {
            StringTokenizer stringTokenizer = new StringTokenizer(property, ",");
            while (stringTokenizer.hasMoreTokens()) {
                String strNextToken = stringTokenizer.nextToken();
                int iIndexOf = strNextToken.indexOf(61);
                if (iIndexOf == -1) {
                    if (f60251a == null) {
                        f60251a = new HashMap();
                    }
                    f60251a.put(strNextToken.toLowerCase(), "true");
                } else {
                    String strSubstring = strNextToken.substring(0, iIndexOf);
                    String strSubstring2 = strNextToken.substring(iIndexOf + 1);
                    if (f60251a == null) {
                        f60251a = new HashMap();
                    }
                    f60251a.put(strSubstring.toLowerCase(), strSubstring2.toLowerCase());
                }
            }
        }
    }
}

package ig;

import android.net.InetAddresses;
import android.os.Build;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes4.dex */
public abstract class d {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final Method f24158a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final Pattern f24159b = Pattern.compile("^(((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?)|((?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?))$");

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final Pattern f24160c = Pattern.compile("^(?=.{1,255}$)[0-9A-Za-z](?:(?:[0-9A-Za-z]|-){0,61}[0-9A-Za-z])?(?:\\.[0-9A-Za-z](?:(?:[0-9A-Za-z]|-){0,61}[0-9A-Za-z])?)*\\.?$");

    static {
        Method method = null;
        try {
            if (Build.VERSION.SDK_INT < 29) {
                method = InetAddress.class.getMethod("parseNumericAddress", String.class);
            }
        } catch (Exception unused) {
        }
        f24158a = method;
    }

    public static InetAddress a(String str) throws j {
        if (str.isEmpty()) {
            throw new j(InetAddress.class, str, "Empty address", null);
        }
        try {
            if (Build.VERSION.SDK_INT >= 29) {
                return InetAddresses.parseNumericAddress(str);
            }
            Method method = f24158a;
            if (method != null) {
                return (InetAddress) method.invoke(null, str);
            }
            throw new NoSuchMethodException("parseNumericAddress");
        } catch (IllegalArgumentException e10) {
            throw new j(InetAddress.class, str, null, e10);
        } catch (Exception e11) {
            Throwable cause = e11.getCause();
            if (cause instanceof IllegalArgumentException) {
                throw new j(InetAddress.class, str, null, (Exception) cause);
            }
            try {
                if (f24159b.matcher(str).matches()) {
                    return InetAddress.getByName(str);
                }
                throw new j(InetAddress.class, str, "Not an IP address", null);
            } catch (UnknownHostException e12) {
                throw new j(InetAddress.class, str, null, e12);
            }
        }
    }
}

package ao;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import com.fasterxml.jackson.core.JsonPointer;
import java.util.Locale;

/* JADX INFO: loaded from: classes5.dex */
public abstract class f {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final o f4157a = new o();

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final Object f4158b = new Object();

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static int f4159c;

    public static String a(Context context) {
        int i10;
        StringBuilder sb2 = new StringBuilder();
        sb2.append(context.getPackageName());
        sb2.append(JsonPointer.SEPARATOR);
        synchronized (f4158b) {
            if (f4159c == 0) {
                try {
                    f4159c = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
                } catch (PackageManager.NameNotFoundException unused) {
                    throw new IllegalStateException("Cannot determine package version");
                }
            }
            i10 = f4159c;
        }
        sb2.append(i10);
        sb2.append(" (Linux; U; Android ");
        sb2.append(Build.VERSION.RELEASE);
        sb2.append("; ");
        sb2.append(Locale.getDefault().toString());
        String str = Build.MODEL;
        if (str.length() > 0) {
            sb2.append("; ");
            sb2.append(str);
        }
        String str2 = Build.ID;
        if (str2.length() > 0) {
            sb2.append("; Build/");
            sb2.append(str2);
        }
        sb2.append("; Cronet/105.0.5195.68)");
        return sb2.toString();
    }
}

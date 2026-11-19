package f6;

import android.content.pm.PackageInfo;
import android.os.Build;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes3.dex */
public final class i extends c {

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final Pattern f16204d;

    public i() {
        super("ALGORITHMIC_DARKENING", "ALGORITHMIC_DARKENING");
        this.f16204d = Pattern.compile("\\A\\d+");
    }

    @Override // f6.c
    public final boolean a() {
        return Build.VERSION.SDK_INT >= 33;
    }

    @Override // f6.c
    public final boolean b() {
        int i10;
        PackageInfo packageInfoA;
        boolean zB = super.b();
        if (!zB || (i10 = Build.VERSION.SDK_INT) >= 29) {
            return zB;
        }
        int i11 = e6.c.f13949a;
        if (i10 >= 26) {
            packageInfoA = d.a();
        } else {
            try {
                packageInfoA = e6.c.a();
            } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException unused) {
                packageInfoA = null;
            }
        }
        if (packageInfoA == null) {
            return false;
        }
        Matcher matcher = this.f16204d.matcher(packageInfoA.versionName);
        return matcher.find() && Integer.parseInt(packageInfoA.versionName.substring(matcher.start(), matcher.end())) >= 105;
    }
}

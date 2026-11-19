package org.chromium.base;

import android.app.UiModeManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import androidx.core.app.i0;
import androidx.media3.exoplayer.rtsp.SessionDescription;
import java.util.Locale;
import kotlin.jvm.internal.Intrinsics;
import org.chromium.base.annotations.CalledByNative;
import pa.a;

/* JADX INFO: loaded from: classes5.dex */
public class BuildInfo {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f44097a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f44098b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final String f44099c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final long f44100d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final String f44101e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public final String f44102f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public final String f44103g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public final String f44104h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final String f44105i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public final String f44106j;
    public final String k;

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    public final boolean f44107l;

    public BuildInfo() {
        PackageInfo packageInfo;
        String strValueOf;
        try {
            Context context = a.f44383e;
            String packageName = context.getPackageName();
            PackageManager packageManager = context.getPackageManager();
            boolean z2 = false;
            PackageInfo packageInfo2 = packageManager.getPackageInfo(packageName, 0);
            long jG = Build.VERSION.SDK_INT >= 28 ? i0.g(packageInfo2) : packageInfo2.versionCode;
            this.f44098b = jG;
            this.f44099c = packageName;
            this.f44100d = jG;
            String str = packageInfo2.versionName;
            String string = "";
            this.f44101e = str == null ? "" : str.toString();
            context.getApplicationInfo();
            CharSequence applicationLabel = packageManager.getApplicationLabel(packageInfo2.applicationInfo);
            this.f44097a = applicationLabel == null ? "" : applicationLabel.toString();
            String installerPackageName = packageManager.getInstallerPackageName(packageName);
            if (installerPackageName != null) {
                string = installerPackageName.toString();
            }
            this.f44102f = string;
            try {
                packageInfo = packageManager.getPackageInfo("com.google.android.gms", 0);
            } catch (PackageManager.NameNotFoundException unused) {
                packageInfo = null;
            }
            if (packageInfo != null) {
                strValueOf = String.valueOf(Build.VERSION.SDK_INT >= 28 ? i0.g(packageInfo) : packageInfo.versionCode);
            } else {
                strValueOf = "gms versionCode not available.";
            }
            this.f44103g = strValueOf;
            String str2 = "true";
            try {
                packageManager.getPackageInfo("projekt.substratum", 0);
            } catch (PackageManager.NameNotFoundException unused2) {
                str2 = "false";
            }
            this.f44106j = str2;
            this.k = "Not Enabled";
            this.f44104h = TextUtils.join(", ", Build.SUPPORTED_ABIS);
            String str3 = Build.FINGERPRINT;
            this.f44105i = str3.substring(0, Math.min(str3.length(), 128));
            UiModeManager uiModeManager = (UiModeManager) context.getSystemService("uimode");
            if (uiModeManager != null && uiModeManager.getCurrentModeType() == 4) {
                z2 = true;
            }
            this.f44107l = z2;
        } catch (PackageManager.NameNotFoundException e10) {
            throw new RuntimeException(e10);
        }
    }

    @CalledByNative
    public static String[] getAll() {
        String str;
        String str2;
        String str3;
        String str4;
        BuildInfo buildInfo = vn.a.f61116a;
        String packageName = a.f44383e.getPackageName();
        String str5 = Build.BRAND;
        String str6 = Build.DEVICE;
        String str7 = Build.ID;
        String str8 = Build.MANUFACTURER;
        String str9 = Build.MODEL;
        int i10 = Build.VERSION.SDK_INT;
        String strValueOf = String.valueOf(i10);
        String str10 = Build.TYPE;
        String str11 = Build.BOARD;
        String strValueOf2 = String.valueOf(buildInfo.f44098b);
        String str12 = buildInfo.f44097a;
        String str13 = buildInfo.f44099c;
        String strValueOf3 = String.valueOf(buildInfo.f44100d);
        String str14 = buildInfo.f44101e;
        Integer num = 0;
        String str15 = buildInfo.f44105i;
        String str16 = buildInfo.f44103g;
        String str17 = buildInfo.f44102f;
        String str18 = buildInfo.f44104h;
        String str19 = buildInfo.f44106j;
        String str20 = buildInfo.k;
        String strValueOf4 = String.valueOf(a.f44383e.getApplicationInfo().targetSdkVersion);
        boolean zEquals = "eng".equals(str10);
        String str21 = "1";
        String str22 = SessionDescription.SUPPORTED_SDP_VERSION;
        if (!zEquals && !"userdebug".equals(str10)) {
            str21 = SessionDescription.SUPPORTED_SDP_VERSION;
        }
        if (buildInfo.f44107l) {
            str22 = "1";
        }
        String str23 = Build.VERSION.INCREMENTAL;
        String str24 = Build.HARDWARE;
        int i11 = f4.a.f16182a;
        String str25 = str21;
        if (i10 < 33) {
            if (i10 >= 32) {
                String buildCodename = Build.VERSION.CODENAME;
                Intrinsics.checkNotNullExpressionValue(buildCodename, "CODENAME");
                str4 = SessionDescription.SUPPORTED_SDP_VERSION;
                Intrinsics.checkNotNullParameter("Tiramisu", "codename");
                Intrinsics.checkNotNullParameter(buildCodename, "buildCodename");
                if (!Intrinsics.areEqual("REL", buildCodename)) {
                    Locale locale = Locale.ROOT;
                    str = strValueOf3;
                    String upperCase = buildCodename.toUpperCase(locale);
                    str2 = str14;
                    Intrinsics.checkNotNullExpressionValue(upperCase, "this as java.lang.String).toUpperCase(Locale.ROOT)");
                    str3 = str7;
                    Integer num2 = Intrinsics.areEqual(upperCase, "BAKLAVA") ? null : null;
                    String upperCase2 = "Tiramisu".toUpperCase(locale);
                    Intrinsics.checkNotNullExpressionValue(upperCase2, "this as java.lang.String).toUpperCase(Locale.ROOT)");
                    if (!Intrinsics.areEqual(upperCase2, "BAKLAVA")) {
                        num = null;
                    }
                    if (num2 == null || num == null) {
                        if (num2 == null && num == null) {
                            String upperCase3 = buildCodename.toUpperCase(locale);
                            Intrinsics.checkNotNullExpressionValue(upperCase3, "this as java.lang.String).toUpperCase(Locale.ROOT)");
                            String upperCase4 = "Tiramisu".toUpperCase(locale);
                            Intrinsics.checkNotNullExpressionValue(upperCase4, "this as java.lang.String).toUpperCase(Locale.ROOT)");
                            if (upperCase3.compareTo(upperCase4) < 0) {
                            }
                        } else if (num2 == null) {
                        }
                    } else if (num2.intValue() < num.intValue()) {
                    }
                }
                str25 = str4;
            } else {
                str4 = str22;
            }
            str = strValueOf3;
            str2 = str14;
            str3 = str7;
            str25 = str4;
        } else {
            str = strValueOf3;
            str2 = str14;
            str3 = str7;
        }
        return new String[]{str5, str6, str3, str8, str9, strValueOf, str10, str11, packageName, strValueOf2, str12, str13, str, str2, str15, str16, str17, str18, "", str19, str20, strValueOf4, str21, str22, str23, str24, str25};
    }
}

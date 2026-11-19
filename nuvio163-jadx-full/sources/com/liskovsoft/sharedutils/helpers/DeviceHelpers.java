package com.liskovsoft.sharedutils.helpers;

import android.app.ActivityManager;
import android.content.Context;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;
import android.provider.Settings;
import android.util.TypedValue;
import android.webkit.CookieManager;

/* JADX INFO: loaded from: classes4.dex */
public final class DeviceHelpers {
    private static final String AMAZON_FEATURE_FIRE_TV = "amazon.hardware.fire_tv";
    private static final boolean BRAVIA_ATV2;
    private static final boolean BRAVIA_ATV3_4K;
    private static final boolean BRAVIA_VH1;
    private static final boolean BRAVIA_VH2;
    private static final boolean CVT_MT5886_EU_1G;
    private static final boolean HI3798MV200;
    private static final boolean HMB9213NW;
    public static final int MEDIA_TUNNELING_DEVICE_BLACKLIST_VERSION = 994;
    private static final String MIME_AV1 = "video/av01";
    private static final String MIME_VP9 = "video/x-vnd.on2.vp9";
    private static final boolean PH7M_EU_5596;
    private static final boolean QM16XE_U;
    private static final boolean REALTEKATV;
    private static final boolean TX_50JXW834;
    private static int sAV1MaxHeight;
    private static Boolean sIsAV1Supported;
    private static Boolean sIsVP9Supported;
    private static int sVP9MaxHeight;
    private static final boolean SAMSUNG = Build.MANUFACTURER.equals("samsung");
    private static Boolean isTV = null;
    private static Boolean isFireTV = null;
    private static int sMaxHeapMemoryMB = -1;
    private static long sCachedRamSize = -1;

    public enum ArchType {
        ARM_V7,
        ARM_64,
        X86,
        X86_64,
        MIPS,
        UNKNOWN
    }

    static {
        int i10 = Build.VERSION.SDK_INT;
        HI3798MV200 = i10 == 24 && Build.DEVICE.equals("Hi3798MV200");
        CVT_MT5886_EU_1G = i10 == 24 && Build.DEVICE.equals("cvt_mt5886_eu_1g");
        REALTEKATV = i10 == 25 && Build.DEVICE.equals("RealtekATV");
        PH7M_EU_5596 = i10 >= 26 && Build.DEVICE.equals("PH7M_EU_5596");
        QM16XE_U = false;
        BRAVIA_VH1 = i10 == 29 && Build.DEVICE.equals("BRAVIA_VH1");
        BRAVIA_VH2 = i10 == 29 && Build.DEVICE.equals("BRAVIA_VH2");
        String str = Build.DEVICE;
        BRAVIA_ATV2 = str.equals("BRAVIA_ATV2");
        BRAVIA_ATV3_4K = str.equals("BRAVIA_ATV3_4K");
        TX_50JXW834 = str.equals("TX_50JXW834");
        HMB9213NW = str.equals("HMB9213NW");
    }

    private DeviceHelpers() {
    }

    public static int dpToPx(int i10, Context context) {
        return (int) TypedValue.applyDimension(1, i10, context.getResources().getDisplayMetrics());
    }

    public static int getAllocatedHeapMemoryMB() {
        return (int) (Runtime.getRuntime().totalMemory() / 1048576);
    }

    public static ArchType getArchType() {
        String primaryAbi = getPrimaryAbi();
        if (primaryAbi == null) {
            return ArchType.UNKNOWN;
        }
        String lowerCase = primaryAbi.toLowerCase();
        if (lowerCase.startsWith("armeabi-v7") || lowerCase.startsWith("armeabi")) {
            return ArchType.ARM_V7;
        }
        if (lowerCase.startsWith("arm64")) {
            return ArchType.ARM_64;
        }
        if (lowerCase.startsWith("x86_64")) {
            return ArchType.X86_64;
        }
        if (lowerCase.startsWith("x86")) {
            return ArchType.X86;
        }
        return lowerCase.startsWith("mips") ? ArchType.MIPS : ArchType.UNKNOWN;
    }

    private static int getCodecMaxHeight(String str) {
        try {
            for (MediaCodecInfo mediaCodecInfo : new MediaCodecList(1).getCodecInfos()) {
                if (!mediaCodecInfo.isEncoder() && isHardwareAccelerated(mediaCodecInfo.getName())) {
                    for (String str2 : mediaCodecInfo.getSupportedTypes()) {
                        if (str2.equalsIgnoreCase(str)) {
                            return ((Integer) mediaCodecInfo.getCapabilitiesForType(str2).getVideoCapabilities().getSupportedHeights().getUpper()).intValue();
                        }
                    }
                }
            }
            return -1;
        } catch (RuntimeException unused) {
            return -1;
        }
    }

    public static long getDeviceRam(Context context) {
        long j10;
        long j11 = sCachedRamSize;
        if (j11 != -1) {
            return j11;
        }
        if (context == null) {
            return -1L;
        }
        ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        if (activityManager != null) {
            activityManager.getMemoryInfo(memoryInfo);
            j10 = memoryInfo.totalMem;
        } else {
            j10 = 500000000;
        }
        sCachedRamSize = j10;
        return j10;
    }

    public static int getMaxHeapMemoryMB() {
        if (sMaxHeapMemoryMB == -1) {
            sMaxHeapMemoryMB = (int) (Runtime.getRuntime().maxMemory() / 1048576);
        }
        return sMaxHeapMemoryMB;
    }

    public static String getPrimaryAbi() {
        String[] strArr = Build.SUPPORTED_ABIS;
        return (strArr == null || strArr.length <= 0) ? Build.CPU_ABI : strArr[0];
    }

    public static boolean hasAnimationsAnimatorDurationEnabled(Context context) {
        return Settings.System.getFloat(context.getContentResolver(), "animator_duration_scale", 1.0f) != 0.0f;
    }

    public static boolean isAV1ResolutionSupported(int i10) {
        if (i10 <= 0) {
            return false;
        }
        if (sAV1MaxHeight == 0) {
            int codecMaxHeight = getCodecMaxHeight("video/av01");
            sAV1MaxHeight = codecMaxHeight;
            if (codecMaxHeight > 0 && codecMaxHeight < 1080) {
                sAV1MaxHeight = 2160;
            }
        }
        return i10 <= sAV1MaxHeight;
    }

    public static boolean isAV1Supported() {
        if (sIsAV1Supported == null) {
            sIsAV1Supported = Boolean.valueOf(getCodecMaxHeight("video/av01") != -1);
        }
        return sIsAV1Supported.booleanValue();
    }

    public static boolean isConfirmKey(int i10) {
        return i10 == 23 || i10 == 62 || i10 == 66 || i10 == 160;
    }

    public static boolean isHardwareAccelerated(String str) {
        if (str == null) {
            return false;
        }
        String[] strArr = {"omx.google.", "c2.android."};
        for (int i10 = 0; i10 < 2; i10++) {
            if (str.toLowerCase().startsWith(strArr[i10])) {
                return false;
            }
        }
        return true;
    }

    public static boolean isLandscape(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels < context.getResources().getDisplayMetrics().widthPixels;
    }

    public static boolean isMemoryCritical() {
        return ((double) getAllocatedHeapMemoryMB()) > ((double) getMaxHeapMemoryMB()) * 0.5d;
    }

    private static boolean isTCL() {
        return Build.MANUFACTURER.toLowerCase().contains("tcl") || Build.BRAND.toLowerCase().contains("tcl");
    }

    public static boolean isVP9ResolutionSupported(int i10) {
        if (i10 <= 0) {
            return false;
        }
        if (sVP9MaxHeight == 0) {
            String str = Build.MODEL;
            str.getClass();
            switch (str) {
                case "PATH_7XPRO":
                case "VIDAA_TV":
                case "MiTV-AXSO0":
                    sVP9MaxHeight = 2160;
                    break;
                case "Chromecast HD":
                case "AFTSSS":
                    sVP9MaxHeight = 1080;
                    break;
                default:
                    sVP9MaxHeight = getCodecMaxHeight("video/x-vnd.on2.vp9");
                    break;
            }
        }
        return i10 <= sVP9MaxHeight;
    }

    public static boolean isVP9Supported() {
        if (sIsVP9Supported == null) {
            sIsVP9Supported = Boolean.valueOf(getCodecMaxHeight("video/x-vnd.on2.vp9") != -1);
        }
        return sIsVP9Supported.booleanValue();
    }

    private static boolean isWebViewBroken() {
        return false;
    }

    public static boolean isWebViewSupported() {
        try {
            CookieManager.getInstance();
            return !isWebViewBroken();
        } catch (Throwable unused) {
            return false;
        }
    }

    public static boolean shouldSupportMediaTunneling() {
        return (HI3798MV200 || CVT_MT5886_EU_1G || REALTEKATV || QM16XE_U || BRAVIA_VH1 || BRAVIA_VH2 || BRAVIA_ATV2 || BRAVIA_ATV3_4K || PH7M_EU_5596 || TX_50JXW834 || HMB9213NW) ? false : true;
    }

    public static int spToPx(int i10, Context context) {
        return (int) TypedValue.applyDimension(2, i10, context.getResources().getDisplayMetrics());
    }
}

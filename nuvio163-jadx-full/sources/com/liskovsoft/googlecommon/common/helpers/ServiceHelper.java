package com.liskovsoft.googlecommon.common.helpers;

import android.annotation.SuppressLint;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import androidx.media3.exoplayer.rtsp.SessionDescription;
import com.liskovsoft.mediaserviceinterfaces.data.MediaGroup;
import com.liskovsoft.sharedutils.helpers.Helpers;
import f9.a;
import i4.b;
import i4.f;
import io.ktor.util.date.GMTDateParser;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes4.dex */
public class ServiceHelper {
    private static final String BULLET_SYMBOL = "•";
    private static final String ITEMS_DIVIDER = " • ";
    private static final String TIME_TEXT_DELIM = ":";
    private static final Pattern sIsoDurationPattern = Pattern.compile("PT(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)S)?");

    public interface OnElementCallback<T> {
        void onElement(T t10, int i10);
    }

    public static boolean atLeastOneEquals(String str, String... strArr) {
        if (str != null && strArr != null) {
            for (String str2 : strArr) {
                if (str.equals(str2)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String channelIdToFullUrl(String str) {
        if (str == null) {
            return null;
        }
        return "https://www.youtube.com/channel/".concat(str);
    }

    public static boolean checkNonNull(Object... objArr) {
        if (objArr == null) {
            return false;
        }
        for (Object obj : objArr) {
            if (obj == null) {
                return false;
            }
        }
        return true;
    }

    /* JADX WARN: Code duplicated, block: B:8:0x0013  */
    public static CharSequence combineItems(CharSequence charSequence, Object... objArr) {
        SpannableStringBuilder spannableStringBuilder;
        SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder();
        if (objArr != null) {
            for (Object obj : objArr) {
                if (obj != null) {
                    CharSequence string = obj instanceof CharSequence ? (CharSequence) obj : obj.toString();
                    if (!TextUtils.isEmpty(string)) {
                        String str = b.f23772b;
                        b bVar = TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == 1 ? b.f23775e : b.f23774d;
                        bVar.getClass();
                        a aVar = f.f23784c;
                        String str2 = b.f23773c;
                        String str3 = b.f23772b;
                        boolean z2 = bVar.f23776a;
                        if (string == null) {
                            spannableStringBuilder = null;
                        } else {
                            boolean zB = aVar.b(string, string.length());
                            spannableStringBuilder = new SpannableStringBuilder();
                            boolean zB2 = (zB ? f.f23783b : f.f23782a).b(string, string.length());
                            spannableStringBuilder.append((CharSequence) ((z2 || !(zB2 || b.a(string) == 1)) ? (!z2 || (zB2 && b.a(string) != -1)) ? "" : str2 : str3));
                            if (zB != z2) {
                                spannableStringBuilder.append(zB ? (char) 8235 : (char) 8234);
                                spannableStringBuilder.append(string);
                                spannableStringBuilder.append((char) 8236);
                            } else {
                                spannableStringBuilder.append(string);
                            }
                            boolean zB3 = (zB ? f.f23783b : f.f23782a).b(string, string.length());
                            if (!z2 && (zB3 || b.b(string) == 1)) {
                                str2 = str3;
                            } else if (!z2 || (zB3 && b.b(string) != -1)) {
                                str2 = "";
                            }
                            spannableStringBuilder.append((CharSequence) str2);
                        }
                        if (charSequence == null || spannableStringBuilder2.length() == 0) {
                            spannableStringBuilder2.append((CharSequence) spannableStringBuilder);
                        } else {
                            spannableStringBuilder2.append(charSequence).append((CharSequence) spannableStringBuilder);
                        }
                    }
                }
            }
        }
        if (spannableStringBuilder2.length() != 0) {
            return spannableStringBuilder2;
        }
        return null;
    }

    public static CharSequence combineText(Object... objArr) {
        return combineItems(null, objArr);
    }

    @SuppressLint({"DefaultLocale"})
    public static String convertIsoDurationToHHMMSS(String str) {
        if (str == null) {
            return null;
        }
        Matcher matcher = sIsoDurationPattern.matcher(str);
        if (!matcher.matches()) {
            return null;
        }
        String strGroup = matcher.group(1);
        String strGroup2 = SessionDescription.SUPPORTED_SDP_VERSION;
        String strGroup3 = strGroup != null ? matcher.group(1) : SessionDescription.SUPPORTED_SDP_VERSION;
        String strGroup4 = matcher.group(2) != null ? matcher.group(2) : SessionDescription.SUPPORTED_SDP_VERSION;
        if (matcher.group(3) != null) {
            strGroup2 = matcher.group(3);
        }
        int i10 = Integer.parseInt(strGroup3);
        int i11 = Integer.parseInt(strGroup4);
        int i12 = Integer.parseInt(strGroup2);
        return i10 > 0 ? String.format("%d:%02d:%02d", Integer.valueOf(i10), Integer.valueOf(i11), Integer.valueOf(i12)) : String.format("%d:%02d", Integer.valueOf(i11), Integer.valueOf(i12));
    }

    public static CharSequence createInfo(Object... objArr) {
        return combineItems(ITEMS_DIVIDER, objArr);
    }

    public static List<MediaGroup> extractEmpty(List<MediaGroup> list) {
        ArrayList arrayList = new ArrayList();
        for (MediaGroup mediaGroup : list) {
            if (mediaGroup.isEmpty()) {
                arrayList.add(mediaGroup);
            }
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            list.remove((MediaGroup) it.next());
        }
        return arrayList;
    }

    public static String generateRandomId(int i10) {
        StringBuilder sb2 = new StringBuilder(i10);
        Random random = new Random();
        for (int i11 = 0; i11 < i10; i11++) {
            sb2.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".charAt(random.nextInt(62)));
        }
        return sb2.toString();
    }

    public static <T> T getFirst(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public static String getToken(String str) {
        if (str == null) {
            return null;
        }
        String[] strArrSplit = str.split("\\s+");
        return strArrSplit.length == 2 ? strArrSplit[1] : strArrSplit[0];
    }

    public static String insertSeparator(String str, String str2, int i10) {
        StringBuilder sb2 = new StringBuilder();
        int i11 = 0;
        for (int i12 = 0; i12 < str.length(); i12++) {
            i11++;
            sb2.append(str.charAt(i12));
            if (i11 % i10 == 0 && i11 != str.length()) {
                sb2.append(str2);
            }
        }
        return sb2.toString();
    }

    public static String millisToTimeText(long j10) {
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        long hours = timeUnit.toHours(j10);
        long minutes = timeUnit.toMinutes(j10);
        TimeUnit timeUnit2 = TimeUnit.HOURS;
        long minutes2 = minutes - timeUnit2.toMinutes(hours);
        long seconds = (timeUnit.toSeconds(j10) - TimeUnit.MINUTES.toSeconds(minutes2)) - timeUnit2.toSeconds(hours);
        return hours != 0 ? String.format(Locale.US, "%d%s%02d%s%02d", Long.valueOf(hours), ":", Long.valueOf(minutes2), ":", Long.valueOf(seconds)) : String.format(Locale.US, "%d%s%02d", Long.valueOf(minutes2), ":", Long.valueOf(seconds));
    }

    public static String prettyCount(Number number) {
        char[] cArr = {' ', 'K', GMTDateParser.MONTH, 'B', 'T', 'P', 'E'};
        long jLongValue = number.longValue();
        double d10 = jLongValue;
        int iFloor = (int) Math.floor(Math.log10(d10));
        int i10 = iFloor / 3;
        if (iFloor < 3 || i10 >= 7) {
            return new DecimalFormat("#,##0").format(jLongValue);
        }
        return new DecimalFormat("#0.#").format(d10 / Math.pow(10.0d, i10 * 3)) + cArr[i10];
    }

    public static <T> void process(List<T> list, OnElementCallback<T> onElementCallback) {
        for (int i10 = 0; i10 < list.size(); i10++) {
            onElementCallback.onElement(list.get(i10), i10);
        }
    }

    public static void sleep(long j10) {
        try {
            Thread.sleep(j10);
        } catch (InterruptedException unused) {
        }
    }

    public static String tidyUrl(String str) {
        if (str == null) {
            return null;
        }
        String strUnescapeUrl = unescapeUrl(str);
        return strUnescapeUrl.startsWith("/") ? "https://www.youtube.com".concat(strUnescapeUrl) : strUnescapeUrl;
    }

    public static long timeTextToMillis(String str) {
        if (str == null || str.contains(",")) {
            return -1L;
        }
        String[] strArrSplit = str.split(":");
        int length = strArrSplit.length;
        int i10 = Helpers.parseInt(strArrSplit, length - 3, 0);
        int i11 = Helpers.parseInt(strArrSplit, length - 2, 0);
        return TimeUnit.SECONDS.toMillis(Helpers.parseInt(strArrSplit, length - 1, 0)) + TimeUnit.MINUTES.toMillis(i11) + TimeUnit.HOURS.toMillis(i10);
    }

    public static String toJsonArrayString(String... strArr) {
        return toJsonArrayString(Arrays.asList(strArr));
    }

    public static long toMillis(String str) {
        if (str == null || str.isEmpty()) {
            return 0L;
        }
        return (long) (Helpers.parseFloat(str) * 1000.0f);
    }

    public static String unescapeUrl(String str) {
        return str.replace("\\/", "/").replace("\\x3d", "=");
    }

    public static String videoIdToFullUrl(String str) {
        if (str == null) {
            return null;
        }
        return "https://www.youtube.com/watch?v=".concat(str);
    }

    public static String toJsonArrayString(Collection<String> collection) {
        if (collection == null) {
            return null;
        }
        StringBuilder sb2 = new StringBuilder("[");
        Iterator<String> it = collection.iterator();
        int i10 = 0;
        while (it.hasNext()) {
            sb2.append(String.format("\"%s\"", it.next()));
            if (i10 != collection.size() - 1) {
                sb2.append(",");
            }
            i10++;
        }
        sb2.append("]");
        return sb2.toString();
    }
}

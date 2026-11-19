package com.fasterxml.jackson.databind.util;

import a0.i;
import com.nuvio.tv.domain.model.StreamKt;
import io.ktor.sse.ServerSentEventKt;
import j$.util.DesugarTimeZone;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import o2.a;

/* JADX INFO: loaded from: classes3.dex */
@Deprecated
public class ISO8601Utils {
    protected static final int DEF_8601_LEN = 29;
    private static final TimeZone TIMEZONE_Z = DesugarTimeZone.getTimeZone("UTC");

    private static boolean checkOffset(String str, int i10, char c5) {
        return i10 < str.length() && str.charAt(i10) == c5;
    }

    public static String format(Date date) {
        return format(date, false, TIMEZONE_Z);
    }

    private static int indexOfNonDigit(String str, int i10) {
        while (i10 < str.length()) {
            char cCharAt = str.charAt(i10);
            if (cCharAt < '0' || cCharAt > '9') {
                return i10;
            }
            i10++;
        }
        return str.length();
    }

    public static Date parse(String str, ParsePosition parsePosition) throws ParseException {
        int i10;
        int i11;
        int i12;
        int i13;
        int length;
        TimeZone timeZone;
        char cCharAt;
        Objects.requireNonNull(str);
        try {
            int index = parsePosition.getIndex();
            int i14 = index + 4;
            int i15 = parseInt(str, index, i14);
            if (checkOffset(str, i14, '-')) {
                i14 = index + 5;
            }
            int i16 = i14 + 2;
            int i17 = parseInt(str, i14, i16);
            if (checkOffset(str, i16, '-')) {
                i16 = i14 + 3;
            }
            int i18 = i16 + 2;
            int i19 = parseInt(str, i16, i18);
            boolean zCheckOffset = checkOffset(str, i18, 'T');
            if (!zCheckOffset && str.length() <= i18) {
                GregorianCalendar gregorianCalendar = new GregorianCalendar(i15, i17 - 1, i19);
                parsePosition.setIndex(i18);
                return gregorianCalendar.getTime();
            }
            if (zCheckOffset) {
                int i20 = i16 + 5;
                int i21 = parseInt(str, i16 + 3, i20);
                if (checkOffset(str, i20, ':')) {
                    i20 = i16 + 6;
                }
                int i22 = i20 + 2;
                i11 = parseInt(str, i20, i22);
                if (checkOffset(str, i22, ':')) {
                    i22 = i20 + 3;
                }
                if (str.length() <= i22 || (cCharAt = str.charAt(i22)) == 'Z' || cCharAt == '+' || cCharAt == '-') {
                    i18 = i22;
                    i10 = i21;
                    i12 = 0;
                    i13 = 0;
                } else {
                    int i23 = i22 + 2;
                    i13 = parseInt(str, i22, i23);
                    if (i13 > 59 && i13 < 63) {
                        i13 = 59;
                    }
                    if (checkOffset(str, i23, '.')) {
                        int i24 = i22 + 3;
                        int iIndexOfNonDigit = indexOfNonDigit(str, i22 + 4);
                        int iMin = Math.min(iIndexOfNonDigit, i22 + 6);
                        int i25 = parseInt(str, i24, iMin);
                        int i26 = iMin - i24;
                        if (i26 == 1) {
                            i25 *= 100;
                        } else if (i26 == 2) {
                            i25 *= 10;
                        }
                        i10 = i21;
                        i18 = iIndexOfNonDigit;
                        i12 = i25;
                    } else {
                        i10 = i21;
                        i18 = i23;
                        i12 = 0;
                    }
                }
            } else {
                i10 = 0;
                i11 = 0;
                i12 = 0;
                i13 = 0;
            }
            if (str.length() <= i18) {
                throw new IllegalArgumentException("No time zone indicator");
            }
            char cCharAt2 = str.charAt(i18);
            if (cCharAt2 == 'Z') {
                timeZone = TIMEZONE_Z;
                length = i18 + 1;
            } else {
                if (cCharAt2 != '+' && cCharAt2 != '-') {
                    throw new IndexOutOfBoundsException("Invalid time zone indicator '" + cCharAt2 + "'");
                }
                String strSubstring = str.substring(i18);
                length = i18 + strSubstring.length();
                if ("+0000".equals(strSubstring) || "+00:00".equals(strSubstring)) {
                    timeZone = TIMEZONE_Z;
                } else {
                    String strConcat = "GMT".concat(strSubstring);
                    TimeZone timeZone2 = DesugarTimeZone.getTimeZone(strConcat);
                    String id2 = timeZone2.getID();
                    if (!id2.equals(strConcat) && !id2.replace(ServerSentEventKt.COLON, "").equals(strConcat)) {
                        throw new IndexOutOfBoundsException("Mismatching time zone indicator: " + strConcat + " given, resolves to " + timeZone2.getID());
                    }
                    timeZone = timeZone2;
                }
            }
            GregorianCalendar gregorianCalendar2 = new GregorianCalendar(timeZone);
            gregorianCalendar2.setLenient(false);
            gregorianCalendar2.set(1, i15);
            gregorianCalendar2.set(2, i17 - 1);
            gregorianCalendar2.set(5, i19);
            gregorianCalendar2.set(11, i10);
            gregorianCalendar2.set(12, i11);
            gregorianCalendar2.set(13, i13);
            gregorianCalendar2.set(14, i12);
            parsePosition.setIndex(length);
            return gregorianCalendar2.getTime();
        } catch (Exception e10) {
            String strL = a.l("\"", '\"', str);
            String message = e10.getMessage();
            if (message == null || message.isEmpty()) {
                message = "(" + e10.getClass().getName() + ")";
            }
            ParseException parseException = new ParseException(i.o("Failed to parse date ", strL, ": ", message), parsePosition.getIndex());
            parseException.initCause(e10);
            throw parseException;
        }
    }

    private static int parseInt(String str, int i10, int i11) {
        int i12;
        int i13;
        if (i10 < 0 || i11 > str.length() || i10 > i11) {
            throw new NumberFormatException(str);
        }
        if (i10 < i11) {
            i13 = i10 + 1;
            int iDigit = Character.digit(str.charAt(i10), 10);
            if (iDigit < 0) {
                throw new NumberFormatException("Invalid number: " + str.substring(i10, i11));
            }
            i12 = -iDigit;
        } else {
            i12 = 0;
            i13 = i10;
        }
        while (i13 < i11) {
            int i14 = i13 + 1;
            int iDigit2 = Character.digit(str.charAt(i13), 10);
            if (iDigit2 < 0) {
                throw new NumberFormatException("Invalid number: " + str.substring(i10, i11));
            }
            i12 = (i12 * 10) - iDigit2;
            i13 = i14;
        }
        return -i12;
    }

    public static String format(Date date, boolean z2) {
        return format(date, z2, TIMEZONE_Z);
    }

    @Deprecated
    public static String format(Date date, boolean z2, TimeZone timeZone) {
        return format(date, z2, timeZone, Locale.US);
    }

    public static String format(Date date, boolean z2, TimeZone timeZone, Locale locale) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar(timeZone, locale);
        gregorianCalendar.setTime(date);
        StringBuilder sb2 = new StringBuilder(30);
        sb2.append(String.format("%04d-%02d-%02dT%02d:%02d:%02d", Integer.valueOf(gregorianCalendar.get(1)), Integer.valueOf(gregorianCalendar.get(2) + 1), Integer.valueOf(gregorianCalendar.get(5)), Integer.valueOf(gregorianCalendar.get(11)), Integer.valueOf(gregorianCalendar.get(12)), Integer.valueOf(gregorianCalendar.get(13))));
        if (z2) {
            sb2.append(String.format(".%03d", Integer.valueOf(gregorianCalendar.get(14))));
        }
        int offset = timeZone.getOffset(gregorianCalendar.getTimeInMillis());
        if (offset != 0) {
            int i10 = offset / StreamKt.AUDIO_DELAY_MAX_MS;
            sb2.append(String.format("%c%02d:%02d", Character.valueOf(offset < 0 ? '-' : '+'), Integer.valueOf(Math.abs(i10 / 60)), Integer.valueOf(Math.abs(i10 % 60))));
        } else {
            sb2.append('Z');
        }
        return sb2.toString();
    }
}

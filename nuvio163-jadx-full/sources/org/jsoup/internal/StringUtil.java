package org.jsoup.internal;

import a0.i;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import org.jsoup.helper.Validate;

/* JADX INFO: loaded from: classes5.dex */
public final class StringUtil {
    private static final int InitBuilderSize = 1024;
    private static final int MaxBuilderSize = 8192;
    static final String[] padding = {"", " ", "  ", "   ", "    ", "     ", "      ", "       ", "        ", "         ", "          ", "           ", "            ", "             ", "              ", "               ", "                ", "                 ", "                  ", "                   ", "                    "};
    private static final Pattern extraDotSegmentsPattern = Pattern.compile("^/(?>(?>\\.\\.?/)+)");
    private static final Pattern validUriScheme = Pattern.compile("^[a-zA-Z][a-zA-Z0-9+-.]*:");
    private static final Pattern controlChars = Pattern.compile("[\\x00-\\x1f]*");
    private static final SoftPool<StringBuilder> BuilderPool = new SoftPool<>(new e(2));

    public static class StringJoiner {
        final String separator;

        /* JADX INFO: renamed from: sb, reason: collision with root package name */
        StringBuilder f44228sb = StringUtil.borrowBuilder();
        boolean first = true;

        public StringJoiner(String str) {
            this.separator = str;
        }

        public StringJoiner add(Object obj) {
            Validate.notNull(this.f44228sb);
            if (!this.first) {
                this.f44228sb.append(this.separator);
            }
            this.f44228sb.append(obj);
            this.first = false;
            return this;
        }

        public StringJoiner append(Object obj) {
            Validate.notNull(this.f44228sb);
            this.f44228sb.append(obj);
            return this;
        }

        public String complete() {
            String strReleaseBuilder = StringUtil.releaseBuilder(this.f44228sb);
            this.f44228sb = null;
            return strReleaseBuilder;
        }
    }

    public static void appendNormalisedWhitespace(StringBuilder sb2, String str, boolean z2) {
        int length = str.length();
        int iCharCount = 0;
        boolean z10 = false;
        boolean z11 = false;
        while (iCharCount < length) {
            int iCodePointAt = str.codePointAt(iCharCount);
            if (isActuallyWhitespace(iCodePointAt)) {
                if ((!z2 || z10) && !z11) {
                    sb2.append(' ');
                    z11 = true;
                }
            } else if (!isInvisibleChar(iCodePointAt)) {
                sb2.appendCodePoint(iCodePointAt);
                z11 = false;
                z10 = true;
            }
            iCharCount += Character.charCount(iCodePointAt);
        }
    }

    public static StringBuilder borrowBuilder() {
        return BuilderPool.borrow();
    }

    public static boolean in(String str, String... strArr) {
        for (String str2 : strArr) {
            if (str2.equals(str)) {
                return true;
            }
        }
        return false;
    }

    public static boolean inSorted(String str, String[] strArr) {
        return Arrays.binarySearch(strArr, str) >= 0;
    }

    public static boolean isActuallyWhitespace(int i10) {
        return i10 == 32 || i10 == 9 || i10 == 10 || i10 == 12 || i10 == 13 || i10 == 160;
    }

    public static boolean isAscii(String str) {
        Validate.notNull(str);
        for (int i10 = 0; i10 < str.length(); i10++) {
            if (str.charAt(i10) > 127) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAsciiLetter(char c5) {
        if (c5 < 'a' || c5 > 'z') {
            return c5 >= 'A' && c5 <= 'Z';
        }
        return true;
    }

    public static boolean isBlank(String str) {
        if (str != null && !str.isEmpty()) {
            int length = str.length();
            for (int i10 = 0; i10 < length; i10++) {
                if (!isWhitespace(str.codePointAt(i10))) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isDigit(char c5) {
        return c5 >= '0' && c5 <= '9';
    }

    public static boolean isHexDigit(char c5) {
        if (isDigit(c5)) {
            return true;
        }
        if (c5 < 'a' || c5 > 'f') {
            return c5 >= 'A' && c5 <= 'F';
        }
        return true;
    }

    public static boolean isInvisibleChar(int i10) {
        return i10 == 8203 || i10 == 173;
    }

    public static boolean isNumeric(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        int length = str.length();
        for (int i10 = 0; i10 < length; i10++) {
            if (!isDigit(str.charAt(i10))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isWhitespace(int i10) {
        return i10 == 32 || i10 == 9 || i10 == 10 || i10 == 12 || i10 == 13;
    }

    public static String join(Collection<?> collection, String str) {
        return join(collection.iterator(), str);
    }

    public static Collector<CharSequence, ?, String> joining(String str) {
        return Collector.of(new bp.b(str, 1), new f(), new g(), new a(4), new Collector.Characteristics[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ StringJoiner lambda$joining$1(String str) {
        return new StringJoiner(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ StringJoiner lambda$joining$2(StringJoiner stringJoiner, StringJoiner stringJoiner2) {
        stringJoiner.append(stringJoiner2.complete());
        return stringJoiner;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ StringBuilder lambda$static$0() {
        return new StringBuilder(1024);
    }

    public static String normaliseWhitespace(String str) {
        StringBuilder sbBorrowBuilder = borrowBuilder();
        appendNormalisedWhitespace(sbBorrowBuilder, str, false);
        return releaseBuilder(sbBorrowBuilder);
    }

    public static String padding(int i10) {
        return padding(i10, 30);
    }

    public static String releaseBuilder(StringBuilder sb2) {
        Validate.notNull(sb2);
        String string = sb2.toString();
        releaseBuilderVoid(sb2);
        return string;
    }

    public static void releaseBuilderVoid(StringBuilder sb2) {
        if (sb2.length() <= 8192) {
            sb2.delete(0, sb2.length());
            BuilderPool.release(sb2);
        }
    }

    public static URL resolve(URL url, String str) {
        String strStripControlChars = stripControlChars(str);
        if (strStripControlChars.startsWith("?")) {
            strStripControlChars = url.getPath() + strStripControlChars;
        }
        URL url2 = new URL(url, strStripControlChars);
        String strReplaceFirst = extraDotSegmentsPattern.matcher(url2.getFile()).replaceFirst("/");
        if (url2.getRef() != null) {
            StringBuilder sbS = i.s(strReplaceFirst, "#");
            sbS.append(url2.getRef());
            strReplaceFirst = sbS.toString();
        }
        return new URL(url2.getProtocol(), url2.getHost(), url2.getPort(), strReplaceFirst);
    }

    public static boolean startsWithNewline(String str) {
        return (str == null || str.length() == 0 || str.charAt(0) != '\n') ? false : true;
    }

    private static String stripControlChars(String str) {
        return controlChars.matcher(str).replaceAll("");
    }

    public static String join(Iterator<?> it, String str) {
        if (!it.hasNext()) {
            return "";
        }
        String string = it.next().toString();
        if (!it.hasNext()) {
            return string;
        }
        StringJoiner stringJoiner = new StringJoiner(str);
        stringJoiner.add(string);
        while (it.hasNext()) {
            stringJoiner.add(it.next());
        }
        return stringJoiner.complete();
    }

    public static String padding(int i10, int i11) {
        Validate.isTrue(i10 >= 0, "width must be >= 0");
        Validate.isTrue(i11 >= -1);
        if (i11 != -1) {
            i10 = Math.min(i10, i11);
        }
        String[] strArr = padding;
        if (i10 < strArr.length) {
            return strArr[i10];
        }
        char[] cArr = new char[i10];
        for (int i12 = 0; i12 < i10; i12++) {
            cArr[i12] = ' ';
        }
        return String.valueOf(cArr);
    }

    public static String join(String[] strArr, String str) {
        return join(Arrays.asList(strArr), str);
    }

    public static String resolve(String str, String str2) {
        String strStripControlChars = stripControlChars(str);
        String strStripControlChars2 = stripControlChars(str2);
        try {
            try {
                return resolve(new URL(strStripControlChars), strStripControlChars2).toExternalForm();
            } catch (MalformedURLException unused) {
                return new URL(strStripControlChars2).toExternalForm();
            }
        } catch (MalformedURLException unused2) {
            return validUriScheme.matcher(strStripControlChars2).find() ? strStripControlChars2 : "";
        }
    }
}

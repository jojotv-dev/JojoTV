package g8;

import java.nio.charset.Charset;
import java.util.HashMap;

/* JADX INFO: loaded from: classes4.dex */
public enum c {
    /* JADX INFO: Fake field, exist only in values array */
    Cp437(new int[]{0, 2}, new String[0]),
    /* JADX INFO: Fake field, exist only in values array */
    ISO8859_1(new int[]{1, 3}, "ISO-8859-1"),
    /* JADX INFO: Fake field, exist only in values array */
    ISO8859_2(4, "ISO-8859-2"),
    /* JADX INFO: Fake field, exist only in values array */
    ISO8859_3(5, "ISO-8859-3"),
    /* JADX INFO: Fake field, exist only in values array */
    ISO8859_4(6, "ISO-8859-4"),
    /* JADX INFO: Fake field, exist only in values array */
    ISO8859_5(7, "ISO-8859-5"),
    /* JADX INFO: Fake field, exist only in values array */
    ISO8859_6(8, "ISO-8859-6"),
    /* JADX INFO: Fake field, exist only in values array */
    ISO8859_7(9, "ISO-8859-7"),
    /* JADX INFO: Fake field, exist only in values array */
    ISO8859_8(10, "ISO-8859-8"),
    /* JADX INFO: Fake field, exist only in values array */
    ISO8859_9(11, "ISO-8859-9"),
    /* JADX INFO: Fake field, exist only in values array */
    ISO8859_10(12, "ISO-8859-10"),
    /* JADX INFO: Fake field, exist only in values array */
    ISO8859_11(13, "ISO-8859-11"),
    /* JADX INFO: Fake field, exist only in values array */
    ISO8859_13(15, "ISO-8859-13"),
    /* JADX INFO: Fake field, exist only in values array */
    ISO8859_14(16, "ISO-8859-14"),
    /* JADX INFO: Fake field, exist only in values array */
    ISO8859_15(17, "ISO-8859-15"),
    /* JADX INFO: Fake field, exist only in values array */
    ISO8859_16(18, "ISO-8859-16"),
    /* JADX INFO: Fake field, exist only in values array */
    SJIS(20, "Shift_JIS"),
    /* JADX INFO: Fake field, exist only in values array */
    Cp1250(21, "windows-1250"),
    /* JADX INFO: Fake field, exist only in values array */
    Cp1251(22, "windows-1251"),
    /* JADX INFO: Fake field, exist only in values array */
    Cp1252(23, "windows-1252"),
    /* JADX INFO: Fake field, exist only in values array */
    Cp1256(24, "windows-1256"),
    /* JADX INFO: Fake field, exist only in values array */
    UnicodeBigUnmarked(25, "UTF-16BE", "UnicodeBig"),
    /* JADX INFO: Fake field, exist only in values array */
    UTF8(26, "UTF-8"),
    /* JADX INFO: Fake field, exist only in values array */
    ASCII(new int[]{27, 170}, "US-ASCII"),
    /* JADX INFO: Fake field, exist only in values array */
    Big5(new int[]{28}, new String[0]),
    /* JADX INFO: Fake field, exist only in values array */
    GB18030(29, "GB2312", "EUC_CN", "GBK"),
    /* JADX INFO: Fake field, exist only in values array */
    EUC_KR(30, "EUC-KR");


    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final HashMap f18821e = new HashMap();

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final HashMap f18822i = new HashMap();

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int[] f18824b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final String[] f18825c;

    static {
        for (c cVar : values()) {
            if (Charset.isSupported(cVar.name())) {
                for (int i10 : cVar.f18824b) {
                    f18821e.put(Integer.valueOf(i10), cVar);
                }
                f18822i.put(cVar.name(), cVar);
                for (String str : cVar.f18825c) {
                    f18822i.put(str, cVar);
                }
            }
        }
    }

    c(int i10, String... strArr) {
        this.f18824b = new int[]{i10};
        this.f18825c = strArr;
    }

    public static c a(Charset charset) {
        return (c) f18822i.get(charset.name());
    }

    public final int b() {
        return this.f18824b[0];
    }

    c(int[] iArr, String... strArr) {
        this.f18824b = iArr;
        this.f18825c = strArr;
    }
}

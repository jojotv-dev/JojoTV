package jcifs.internal.util;

/* JADX INFO: loaded from: classes5.dex */
public final class StringUtil {
    private StringUtil() {
    }

    public static String join(CharSequence charSequence, CharSequence... charSequenceArr) {
        StringBuilder sb2 = new StringBuilder();
        for (CharSequence charSequence2 : charSequenceArr) {
            if (sb2.length() > 0) {
                if (charSequence != null) {
                    sb2.append(charSequence);
                } else {
                    sb2.append("null");
                }
            }
            sb2.append(charSequence2);
        }
        return sb2.toString();
    }
}

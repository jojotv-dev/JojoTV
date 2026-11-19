package androidx.media3.extractor.text.ttml;

import android.text.TextUtils;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Pattern;
import kf.ge;
import r7.n1;
import r7.o2;
import r7.p1;
import r7.q2;
import r7.u2;
import r7.z;

/* JADX INFO: loaded from: classes3.dex */
final class TextEmphasis {
    public static final int MARK_SHAPE_AUTO = -1;
    public static final int POSITION_OUTSIDE = -2;
    public final int markFill;
    public final int markShape;
    public final int position;
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");
    private static final n1 SINGLE_STYLE_VALUES = n1.j(2, "auto", "none");
    private static final n1 MARK_SHAPE_VALUES = n1.o(TtmlNode.TEXT_EMPHASIS_MARK_DOT, TtmlNode.TEXT_EMPHASIS_MARK_SESAME, TtmlNode.TEXT_EMPHASIS_MARK_CIRCLE);
    private static final n1 MARK_FILL_VALUES = n1.j(2, TtmlNode.TEXT_EMPHASIS_MARK_FILLED, TtmlNode.TEXT_EMPHASIS_MARK_OPEN);
    private static final n1 POSITION_VALUES = n1.o(TtmlNode.ANNOTATION_POSITION_AFTER, TtmlNode.ANNOTATION_POSITION_BEFORE, TtmlNode.ANNOTATION_POSITION_OUTSIDE);

    @Target({ElementType.TYPE_USE})
    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface Position {
    }

    private TextEmphasis(int i10, int i11, int i12) {
        this.markShape = i10;
        this.markFill = i11;
        this.position = i12;
    }

    public static TextEmphasis parse(String str) {
        n1 n1VarJ;
        if (str == null) {
            return null;
        }
        String strR0 = ge.r0(str.trim());
        if (strR0.isEmpty()) {
            return null;
        }
        String[] strArrSplit = TextUtils.split(strR0, WHITESPACE_PATTERN);
        int length = strArrSplit.length;
        if (length != 0) {
            n1VarJ = length != 1 ? n1.j(strArrSplit.length, (Object[]) strArrSplit.clone()) : new u2(strArrSplit[0]);
        } else {
            n1VarJ = o2.f51326r;
        }
        return parseWords(n1VarJ);
    }

    /* JADX WARN: Code duplicated, block: B:19:0x004b  */
    /* JADX WARN: Code duplicated, block: B:21:0x005f  */
    /* JADX WARN: Code duplicated, block: B:27:0x006f  */
    /* JADX WARN: Code duplicated, block: B:30:0x007b  */
    /* JADX WARN: Code duplicated, block: B:38:0x00aa  */
    /* JADX WARN: Code duplicated, block: B:44:0x00ba  */
    /* JADX WARN: Code duplicated, block: B:48:0x00d0  */
    /* JADX WARN: Code duplicated, block: B:50:0x00d5  */
    /* JADX WARN: Code duplicated, block: B:53:0x00db  */
    /* JADX WARN: Code duplicated, block: B:56:0x00e5  */
    /* JADX WARN: Code duplicated, block: B:58:0x00ed  */
    /* JADX WARN: Code duplicated, block: B:59:0x00ef  */
    private static TextEmphasis parseWords(n1 n1Var) {
        int i10;
        q2 q2VarL;
        int i11;
        q2 q2VarL2;
        q2 q2VarL3;
        String str;
        int iHashCode;
        int i12;
        String str2;
        int iHashCode2;
        String str3;
        int iHashCode3;
        String str4 = (String) z.h(z.l(POSITION_VALUES, n1Var), TtmlNode.ANNOTATION_POSITION_OUTSIDE);
        int iHashCode4 = str4.hashCode();
        int i13 = 1;
        if (iHashCode4 != -1392885889) {
            if (iHashCode4 != -1106037339) {
                if (iHashCode4 == 92734940 && str4.equals(TtmlNode.ANNOTATION_POSITION_AFTER)) {
                    i10 = 2;
                }
            } else if (str4.equals(TtmlNode.ANNOTATION_POSITION_OUTSIDE)) {
                i10 = -2;
            }
            q2VarL = z.l(SINGLE_STYLE_VALUES, n1Var);
            i11 = -1;
            if (!q2VarL.isEmpty()) {
                str3 = (String) new p1(q2VarL).next();
                iHashCode3 = str3.hashCode();
                if (iHashCode3 != 3005871) {
                    str3.equals("auto");
                } else if (iHashCode3 == 3387192 && str3.equals("none")) {
                    i11 = 0;
                }
                return new TextEmphasis(i11, 0, i10);
            }
            q2VarL2 = z.l(MARK_FILL_VALUES, n1Var);
            q2VarL3 = z.l(MARK_SHAPE_VALUES, n1Var);
            if (!q2VarL2.isEmpty() && q2VarL3.isEmpty()) {
                return new TextEmphasis(-1, 0, i10);
            }
            str = (String) z.h(q2VarL2, TtmlNode.TEXT_EMPHASIS_MARK_FILLED);
            iHashCode = str.hashCode();
            if (iHashCode != -1274499742) {
                if (iHashCode == 3417674 && str.equals(TtmlNode.TEXT_EMPHASIS_MARK_OPEN)) {
                    i12 = 2;
                }
                str2 = (String) z.h(q2VarL3, TtmlNode.TEXT_EMPHASIS_MARK_CIRCLE);
                iHashCode2 = str2.hashCode();
                if (iHashCode2 != -1360216880) {
                    str2.equals(TtmlNode.TEXT_EMPHASIS_MARK_CIRCLE);
                } else if (iHashCode2 != -905816648) {
                    if (iHashCode2 == 99657 && str2.equals(TtmlNode.TEXT_EMPHASIS_MARK_DOT)) {
                        i13 = 2;
                    }
                } else if (str2.equals(TtmlNode.TEXT_EMPHASIS_MARK_SESAME)) {
                    i13 = 3;
                }
                return new TextEmphasis(i13, i12, i10);
            }
            str.equals(TtmlNode.TEXT_EMPHASIS_MARK_FILLED);
            i12 = 1;
            str2 = (String) z.h(q2VarL3, TtmlNode.TEXT_EMPHASIS_MARK_CIRCLE);
            iHashCode2 = str2.hashCode();
            if (iHashCode2 != -1360216880) {
                str2.equals(TtmlNode.TEXT_EMPHASIS_MARK_CIRCLE);
            } else if (iHashCode2 != -905816648) {
                if (iHashCode2 == 99657) {
                    i13 = 2;
                }
            } else if (str2.equals(TtmlNode.TEXT_EMPHASIS_MARK_SESAME)) {
                i13 = 3;
            }
            return new TextEmphasis(i13, i12, i10);
        }
        str4.equals(TtmlNode.ANNOTATION_POSITION_BEFORE);
        i10 = 1;
        q2VarL = z.l(SINGLE_STYLE_VALUES, n1Var);
        i11 = -1;
        if (!q2VarL.isEmpty()) {
            str3 = (String) new p1(q2VarL).next();
            iHashCode3 = str3.hashCode();
            if (iHashCode3 != 3005871) {
                str3.equals("auto");
            } else if (iHashCode3 == 3387192) {
                i11 = 0;
            }
            return new TextEmphasis(i11, 0, i10);
        }
        q2VarL2 = z.l(MARK_FILL_VALUES, n1Var);
        q2VarL3 = z.l(MARK_SHAPE_VALUES, n1Var);
        if (!q2VarL2.isEmpty()) {
        }
        str = (String) z.h(q2VarL2, TtmlNode.TEXT_EMPHASIS_MARK_FILLED);
        iHashCode = str.hashCode();
        if (iHashCode != -1274499742) {
            if (iHashCode == 3417674) {
                i12 = 2;
            }
            str2 = (String) z.h(q2VarL3, TtmlNode.TEXT_EMPHASIS_MARK_CIRCLE);
            iHashCode2 = str2.hashCode();
            if (iHashCode2 != -1360216880) {
                str2.equals(TtmlNode.TEXT_EMPHASIS_MARK_CIRCLE);
            } else if (iHashCode2 != -905816648) {
                if (iHashCode2 == 99657) {
                    i13 = 2;
                }
            } else if (str2.equals(TtmlNode.TEXT_EMPHASIS_MARK_SESAME)) {
                i13 = 3;
            }
            return new TextEmphasis(i13, i12, i10);
        }
        str.equals(TtmlNode.TEXT_EMPHASIS_MARK_FILLED);
        i12 = 1;
        str2 = (String) z.h(q2VarL3, TtmlNode.TEXT_EMPHASIS_MARK_CIRCLE);
        iHashCode2 = str2.hashCode();
        if (iHashCode2 != -1360216880) {
            str2.equals(TtmlNode.TEXT_EMPHASIS_MARK_CIRCLE);
        } else if (iHashCode2 != -905816648) {
            if (iHashCode2 == 99657) {
                i13 = 2;
            }
        } else if (str2.equals(TtmlNode.TEXT_EMPHASIS_MARK_SESAME)) {
            i13 = 3;
        }
        return new TextEmphasis(i13, i12, i10);
    }
}

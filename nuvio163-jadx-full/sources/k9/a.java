package k9;

import io.ktor.util.date.GMTDateParser;
import java.util.Collection;
import java.util.EnumSet;

/* JADX INFO: loaded from: classes4.dex */
public abstract class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final char[] f28008a = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', GMTDateParser.DAY_OF_MONTH, 'e', 'f'};

    public static boolean a(Object obj, Object obj2) {
        if (obj != obj2) {
            return obj != null && obj.equals(obj2);
        }
        return true;
    }

    public static boolean b(long j10, c cVar) {
        return (j10 & cVar.getValue()) > 0;
    }

    public static String c(byte[] bArr) {
        StringBuilder sb2 = new StringBuilder();
        for (byte b5 : bArr) {
            if (sb2.length() > 0) {
                sb2.append(' ');
            }
            char[] cArr = f28008a;
            sb2.append(cArr[(b5 >> 4) & 15]);
            sb2.append(cArr[b5 & 15]);
        }
        return sb2.toString();
    }

    public static EnumSet d(long j10, Class cls) {
        if (!c.class.isAssignableFrom(cls)) {
            throw new IllegalArgumentException("Can only be used with EnumWithValue enums.");
        }
        EnumSet enumSetNoneOf = EnumSet.noneOf(cls);
        for (Object obj : (Enum[]) cls.getEnumConstants()) {
            if (b(j10, (c) obj)) {
                enumSetNoneOf.add(obj);
            }
        }
        return enumSetNoneOf;
    }

    public static long e(Collection collection) {
        long value = 0;
        for (Object obj : collection) {
            if (!(obj instanceof c)) {
                throw new IllegalArgumentException("Can only be used with EnumWithValue enums.");
            }
            value |= ((c) obj).getValue();
        }
        return value;
    }

    public static c f(long j10, Class cls, c cVar) {
        for (c cVar2 : (c[]) cls.getEnumConstants()) {
            if (cVar2.getValue() == j10) {
                return cVar2;
            }
        }
        return cVar;
    }
}

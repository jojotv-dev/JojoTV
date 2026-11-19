package xo;

import androidx.media3.container.MdtaMetadataEntry;
import com.fasterxml.jackson.core.JsonPointer;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes5.dex */
public final class k {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final k f64141e = new k(0, null, 1443168256, 1);

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public static final k f64142f = new k(1, null, 1509950721, 1);

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public static final k f64143g = new k(2, null, 1124075009, 1);

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public static final k f64144h = new k(3, null, 1107297537, 1);

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final k f64145i = new k(4, null, 1392510721, 1);

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public static final k f64146j = new k(5, null, 1224736769, 1);
    public static final k k = new k(6, null, 1174536705, 1);

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    public static final k f64147l = new k(7, null, 1241579778, 1);

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final k f64148m = new k(8, null, 1141048066, 1);

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f64149a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final char[] f64150b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f64151c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final int f64152d;

    public k(int i10, char[] cArr, int i11, int i12) {
        this.f64149a = i10;
        this.f64150b = cArr;
        this.f64151c = i11;
        this.f64152d = i12;
    }

    public static k a(int i10, char[] cArr) {
        char c5;
        char c10 = cArr[i10];
        if (c10 == 'F') {
            return k;
        }
        if (c10 == 'L') {
            int i11 = 1;
            while (cArr[i10 + i11] != ';') {
                i11++;
            }
            return new k(10, cArr, i10 + 1, i11 - 1);
        }
        if (c10 == 'S') {
            return f64145i;
        }
        if (c10 == 'V') {
            return f64141e;
        }
        if (c10 == 'I') {
            return f64146j;
        }
        if (c10 == 'J') {
            return f64147l;
        }
        if (c10 == 'Z') {
            return f64142f;
        }
        if (c10 != '[') {
            switch (c10) {
                case 'B':
                    return f64144h;
                case MdtaMetadataEntry.TYPE_INDICATOR_INT32 /* 67 */:
                    return f64143g;
                case 'D':
                    return f64148m;
                default:
                    return new k(11, cArr, i10, cArr.length - i10);
            }
        }
        int i12 = 1;
        while (true) {
            c5 = cArr[i10 + i12];
            if (c5 != '[') {
                break;
            }
            i12++;
        }
        if (c5 == 'L') {
            do {
                i12++;
            } while (cArr[i10 + i12] != ';');
        }
        return new k(9, cArr, i10, i12 + 1);
    }

    public static void b(StringBuffer stringBuffer, Class cls) {
        char c5;
        while (!cls.isPrimitive()) {
            if (!cls.isArray()) {
                stringBuffer.append('L');
                String name = cls.getName();
                int length = name.length();
                for (int i10 = 0; i10 < length; i10++) {
                    char cCharAt = name.charAt(i10);
                    if (cCharAt == '.') {
                        cCharAt = JsonPointer.SEPARATOR;
                    }
                    stringBuffer.append(cCharAt);
                }
                stringBuffer.append(';');
                return;
            }
            stringBuffer.append('[');
            cls = cls.getComponentType();
        }
        if (cls == Integer.TYPE) {
            c5 = 'I';
        } else if (cls == Void.TYPE) {
            c5 = 'V';
        } else if (cls == Boolean.TYPE) {
            c5 = 'Z';
        } else if (cls == Byte.TYPE) {
            c5 = 'B';
        } else if (cls == Character.TYPE) {
            c5 = 'C';
        } else if (cls == Short.TYPE) {
            c5 = 'S';
        } else if (cls == Double.TYPE) {
            c5 = 'D';
        } else {
            c5 = cls == Float.TYPE ? 'F' : 'J';
        }
        stringBuffer.append(c5);
    }

    public static k[] c(String str) {
        char[] charArray = str.toCharArray();
        int i10 = 1;
        int i11 = 1;
        int i12 = 0;
        while (true) {
            int i13 = i11 + 1;
            char c5 = charArray[i11];
            if (c5 == ')') {
                break;
            }
            if (c5 == 'L') {
                while (true) {
                    i11 = i13 + 1;
                    if (charArray[i13] == ';') {
                        break;
                    }
                    i13 = i11;
                }
                i12++;
            } else {
                if (c5 != '[') {
                    i12++;
                }
                i11 = i13;
            }
        }
        k[] kVarArr = new k[i12];
        int i14 = 0;
        while (charArray[i10] != ')') {
            k kVarA = a(i10, charArray);
            kVarArr[i14] = kVarA;
            i10 += kVarA.f64152d + (kVarA.f64149a == 10 ? 2 : 0);
            i14++;
        }
        return kVarArr;
    }

    public static int d(String str) {
        int i10;
        char cCharAt;
        int i11 = 1;
        int i12 = 1;
        int i13 = 1;
        while (true) {
            i10 = i12 + 1;
            char cCharAt2 = str.charAt(i12);
            if (cCharAt2 == ')') {
                break;
            }
            if (cCharAt2 == 'L') {
                while (true) {
                    i12 = i10 + 1;
                    if (str.charAt(i10) == ';') {
                        break;
                    }
                    i10 = i12;
                }
                i13++;
            } else {
                if (cCharAt2 == '[') {
                    while (true) {
                        cCharAt = str.charAt(i10);
                        if (cCharAt != '[') {
                            break;
                        }
                        i10++;
                    }
                    if (cCharAt == 'D' || cCharAt == 'J') {
                        i13--;
                    }
                } else {
                    i13 = (cCharAt2 == 'D' || cCharAt2 == 'J') ? i13 + 2 : i13 + 1;
                }
                i12 = i10;
            }
        }
        char cCharAt3 = str.charAt(i10);
        int i14 = i13 << 2;
        if (cCharAt3 == 'V') {
            i11 = 0;
        } else if (cCharAt3 == 'D' || cCharAt3 == 'J') {
            i11 = 2;
        }
        return i14 | i11;
    }

    public static String f(Class cls) {
        return cls.getName().replace('.', JsonPointer.SEPARATOR);
    }

    public static String g(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append('(');
        for (Class<?> cls : parameterTypes) {
            b(stringBuffer, cls);
        }
        stringBuffer.append(')');
        b(stringBuffer, method.getReturnType());
        return stringBuffer.toString();
    }

    public static k h(Class cls) {
        if (!cls.isPrimitive()) {
            StringBuffer stringBuffer = new StringBuffer();
            b(stringBuffer, cls);
            return a(0, stringBuffer.toString().toCharArray());
        }
        if (cls == Integer.TYPE) {
            return f64146j;
        }
        if (cls == Void.TYPE) {
            return f64141e;
        }
        if (cls == Boolean.TYPE) {
            return f64142f;
        }
        if (cls == Byte.TYPE) {
            return f64144h;
        }
        if (cls == Character.TYPE) {
            return f64143g;
        }
        if (cls == Short.TYPE) {
            return f64145i;
        }
        if (cls == Double.TYPE) {
            return f64148m;
        }
        return cls == Float.TYPE ? k : f64147l;
    }

    public final String e() {
        char c5;
        StringBuffer stringBuffer = new StringBuffer();
        int i10 = this.f64151c;
        char[] cArr = this.f64150b;
        if (cArr != null) {
            int i11 = this.f64149a;
            int i12 = this.f64152d;
            if (i11 == 10) {
                stringBuffer.append('L');
                stringBuffer.append(cArr, i10, i12);
                c5 = ';';
            } else {
                stringBuffer.append(cArr, i10, i12);
            }
            return stringBuffer.toString();
        }
        c5 = (char) ((i10 & (-16777216)) >>> 24);
        stringBuffer.append(c5);
        return stringBuffer.toString();
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof k)) {
            return false;
        }
        k kVar = (k) obj;
        int i10 = kVar.f64149a;
        int i11 = this.f64149a;
        if (i11 != i10) {
            return false;
        }
        if (i11 >= 9) {
            int i12 = kVar.f64152d;
            int i13 = this.f64152d;
            if (i13 != i12) {
                return false;
            }
            int i14 = kVar.f64151c;
            int i15 = this.f64151c;
            int i16 = i13 + i15;
            while (i15 < i16) {
                if (this.f64150b[i15] != kVar.f64150b[i14]) {
                    return false;
                }
                i15++;
                i14++;
            }
        }
        return true;
    }

    public final int hashCode() {
        int i10 = this.f64149a;
        int i11 = i10 * 13;
        if (i10 >= 9) {
            int i12 = this.f64152d;
            int i13 = this.f64151c;
            int i14 = i12 + i13;
            while (i13 < i14) {
                i11 = (i11 + this.f64150b[i13]) * 17;
                i13++;
            }
        }
        return i11;
    }

    public final String toString() {
        return e();
    }
}

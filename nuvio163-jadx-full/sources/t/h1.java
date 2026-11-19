package t;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Map;
import kotlin.collections.ArraysKt;
import kotlin.collections.ArraysKt___ArraysJvmKt;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public class h1 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public int[] f57004b = u.a.f57933a;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public Object[] f57005c = u.a.f57935c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public int f57006e;

    public h1(Object obj) {
    }

    public final int a(Object obj) {
        int i10 = this.f57006e * 2;
        Object[] objArr = this.f57005c;
        if (obj == null) {
            for (int i11 = 1; i11 < i10; i11 += 2) {
                if (objArr[i11] == null) {
                    return i11 >> 1;
                }
            }
            return -1;
        }
        for (int i12 = 1; i12 < i10; i12 += 2) {
            if (Intrinsics.areEqual(obj, objArr[i12])) {
                return i12 >> 1;
            }
        }
        return -1;
    }

    public final void b(int i10) {
        int i11 = this.f57006e;
        int[] iArr = this.f57004b;
        if (iArr.length < i10) {
            int[] iArrCopyOf = Arrays.copyOf(iArr, i10);
            Intrinsics.checkNotNullExpressionValue(iArrCopyOf, "copyOf(...)");
            this.f57004b = iArrCopyOf;
            Object[] objArrCopyOf = Arrays.copyOf(this.f57005c, i10 * 2);
            Intrinsics.checkNotNullExpressionValue(objArrCopyOf, "copyOf(...)");
            this.f57005c = objArrCopyOf;
        }
        if (this.f57006e != i11) {
            throw new ConcurrentModificationException();
        }
    }

    public final int c(int i10, Object obj) {
        int i11 = this.f57006e;
        if (i11 == 0) {
            return -1;
        }
        int iA = u.a.a(i11, i10, this.f57004b);
        if (iA < 0 || Intrinsics.areEqual(obj, this.f57005c[iA << 1])) {
            return iA;
        }
        int i12 = iA + 1;
        while (i12 < i11 && this.f57004b[i12] == i10) {
            if (Intrinsics.areEqual(obj, this.f57005c[i12 << 1])) {
                return i12;
            }
            i12++;
        }
        for (int i13 = iA - 1; i13 >= 0 && this.f57004b[i13] == i10; i13--) {
            if (Intrinsics.areEqual(obj, this.f57005c[i13 << 1])) {
                return i13;
            }
        }
        return ~i12;
    }

    public final void clear() {
        if (this.f57006e > 0) {
            this.f57004b = u.a.f57933a;
            this.f57005c = u.a.f57935c;
            this.f57006e = 0;
        }
        if (this.f57006e > 0) {
            throw new ConcurrentModificationException();
        }
    }

    public boolean containsKey(Object obj) {
        return d(obj) >= 0;
    }

    public boolean containsValue(Object obj) {
        return a(obj) >= 0;
    }

    public final int d(Object obj) {
        return obj == null ? e() : c(obj.hashCode(), obj);
    }

    public final int e() {
        int i10 = this.f57006e;
        if (i10 == 0) {
            return -1;
        }
        int iA = u.a.a(i10, 0, this.f57004b);
        if (iA < 0 || this.f57005c[iA << 1] == null) {
            return iA;
        }
        int i11 = iA + 1;
        while (i11 < i10 && this.f57004b[i11] == 0) {
            if (this.f57005c[i11 << 1] == null) {
                return i11;
            }
            i11++;
        }
        for (int i12 = iA - 1; i12 >= 0 && this.f57004b[i12] == 0; i12--) {
            if (this.f57005c[i12 << 1] == null) {
                return i12;
            }
        }
        return ~i11;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        try {
            if (obj instanceof h1) {
                int i10 = this.f57006e;
                if (i10 != ((h1) obj).f57006e) {
                    return false;
                }
                h1 h1Var = (h1) obj;
                for (int i11 = 0; i11 < i10; i11++) {
                    Object objF = f(i11);
                    Object objI = i(i11);
                    Object obj2 = h1Var.get(objF);
                    if (objI == null) {
                        if (obj2 != null || !h1Var.containsKey(objF)) {
                            return false;
                        }
                    } else if (!Intrinsics.areEqual(objI, obj2)) {
                        return false;
                    }
                }
                return true;
            }
            if (!(obj instanceof Map) || this.f57006e != ((Map) obj).size()) {
                return false;
            }
            int i12 = this.f57006e;
            for (int i13 = 0; i13 < i12; i13++) {
                Object objF2 = f(i13);
                Object objI2 = i(i13);
                Object obj3 = ((Map) obj).get(objF2);
                if (objI2 == null) {
                    if (obj3 != null || !((Map) obj).containsKey(objF2)) {
                        return false;
                    }
                } else if (!Intrinsics.areEqual(objI2, obj3)) {
                    return false;
                }
            }
            return true;
        } catch (ClassCastException | NullPointerException unused) {
        }
        return false;
    }

    public final Object f(int i10) {
        boolean z2 = false;
        if (i10 >= 0 && i10 < this.f57006e) {
            z2 = true;
        }
        if (z2) {
            return this.f57005c[i10 << 1];
        }
        u.a.c("Expected index to be within 0..size()-1, but was " + i10);
        throw null;
    }

    public final Object g(int i10) {
        int i11;
        if (i10 < 0 || i10 >= (i11 = this.f57006e)) {
            u.a.c("Expected index to be within 0..size()-1, but was " + i10);
            throw null;
        }
        Object[] objArr = this.f57005c;
        int i12 = i10 << 1;
        Object obj = objArr[i12 + 1];
        if (i11 <= 1) {
            clear();
            return obj;
        }
        int i13 = i11 - 1;
        int[] iArr = this.f57004b;
        if (iArr.length <= 8 || i11 >= iArr.length / 3) {
            if (i10 < i13) {
                int i14 = i10 + 1;
                ArraysKt___ArraysJvmKt.copyInto(iArr, iArr, i10, i14, i11);
                Object[] objArr2 = this.f57005c;
                ArraysKt.copyInto(objArr2, objArr2, i12, i14 << 1, i11 << 1);
            }
            Object[] objArr3 = this.f57005c;
            int i15 = i13 << 1;
            objArr3[i15] = null;
            objArr3[i15 + 1] = null;
        } else {
            int i16 = i11 > 8 ? i11 + (i11 >> 1) : 8;
            int[] iArrCopyOf = Arrays.copyOf(iArr, i16);
            Intrinsics.checkNotNullExpressionValue(iArrCopyOf, "copyOf(...)");
            this.f57004b = iArrCopyOf;
            Object[] objArrCopyOf = Arrays.copyOf(this.f57005c, i16 << 1);
            Intrinsics.checkNotNullExpressionValue(objArrCopyOf, "copyOf(...)");
            this.f57005c = objArrCopyOf;
            if (i11 != this.f57006e) {
                throw new ConcurrentModificationException();
            }
            if (i10 > 0) {
                ArraysKt___ArraysJvmKt.copyInto(iArr, this.f57004b, 0, 0, i10);
                ArraysKt.copyInto(objArr, this.f57005c, 0, 0, i12);
            }
            if (i10 < i13) {
                int i17 = i10 + 1;
                ArraysKt___ArraysJvmKt.copyInto(iArr, this.f57004b, i10, i17, i11);
                ArraysKt.copyInto(objArr, this.f57005c, i12, i17 << 1, i11 << 1);
            }
        }
        if (i11 != this.f57006e) {
            throw new ConcurrentModificationException();
        }
        this.f57006e = i13;
        return obj;
    }

    public Object get(Object obj) {
        int iD = d(obj);
        if (iD >= 0) {
            return this.f57005c[(iD << 1) + 1];
        }
        return null;
    }

    public final Object getOrDefault(Object obj, Object obj2) {
        int iD = d(obj);
        return iD >= 0 ? this.f57005c[(iD << 1) + 1] : obj2;
    }

    public final Object h(int i10, Object obj) {
        boolean z2 = false;
        if (i10 >= 0 && i10 < this.f57006e) {
            z2 = true;
        }
        if (!z2) {
            u.a.c("Expected index to be within 0..size()-1, but was " + i10);
            throw null;
        }
        int i11 = (i10 << 1) + 1;
        Object[] objArr = this.f57005c;
        Object obj2 = objArr[i11];
        objArr[i11] = obj;
        return obj2;
    }

    public final int hashCode() {
        int[] iArr = this.f57004b;
        Object[] objArr = this.f57005c;
        int i10 = this.f57006e;
        int i11 = 1;
        int i12 = 0;
        int iHashCode = 0;
        while (i12 < i10) {
            Object obj = objArr[i11];
            iHashCode += (obj != null ? obj.hashCode() : 0) ^ iArr[i12];
            i12++;
            i11 += 2;
        }
        return iHashCode;
    }

    public final Object i(int i10) {
        boolean z2 = false;
        if (i10 >= 0 && i10 < this.f57006e) {
            z2 = true;
        }
        if (z2) {
            return this.f57005c[(i10 << 1) + 1];
        }
        u.a.c("Expected index to be within 0..size()-1, but was " + i10);
        throw null;
    }

    public final boolean isEmpty() {
        return this.f57006e <= 0;
    }

    public final Object put(Object obj, Object obj2) {
        int i10 = this.f57006e;
        int iHashCode = obj != null ? obj.hashCode() : 0;
        int iC = obj != null ? c(iHashCode, obj) : e();
        if (iC >= 0) {
            int i11 = (iC << 1) + 1;
            Object[] objArr = this.f57005c;
            Object obj3 = objArr[i11];
            objArr[i11] = obj2;
            return obj3;
        }
        int i12 = ~iC;
        int[] iArr = this.f57004b;
        if (i10 >= iArr.length) {
            int i13 = 8;
            if (i10 >= 8) {
                i13 = (i10 >> 1) + i10;
            } else if (i10 < 4) {
                i13 = 4;
            }
            int[] iArrCopyOf = Arrays.copyOf(iArr, i13);
            Intrinsics.checkNotNullExpressionValue(iArrCopyOf, "copyOf(...)");
            this.f57004b = iArrCopyOf;
            Object[] objArrCopyOf = Arrays.copyOf(this.f57005c, i13 << 1);
            Intrinsics.checkNotNullExpressionValue(objArrCopyOf, "copyOf(...)");
            this.f57005c = objArrCopyOf;
            if (i10 != this.f57006e) {
                throw new ConcurrentModificationException();
            }
        }
        if (i12 < i10) {
            int[] iArr2 = this.f57004b;
            int i14 = i12 + 1;
            ArraysKt___ArraysJvmKt.copyInto(iArr2, iArr2, i14, i12, i10);
            Object[] objArr2 = this.f57005c;
            ArraysKt.copyInto(objArr2, objArr2, i14 << 1, i12 << 1, this.f57006e << 1);
        }
        int i15 = this.f57006e;
        if (i10 == i15) {
            int[] iArr3 = this.f57004b;
            if (i12 < iArr3.length) {
                iArr3[i12] = iHashCode;
                Object[] objArr3 = this.f57005c;
                int i16 = i12 << 1;
                objArr3[i16] = obj;
                objArr3[i16 + 1] = obj2;
                this.f57006e = i15 + 1;
                return null;
            }
        }
        throw new ConcurrentModificationException();
    }

    public final Object putIfAbsent(Object obj, Object obj2) {
        Object obj3 = get(obj);
        return obj3 == null ? put(obj, obj2) : obj3;
    }

    public Object remove(Object obj) {
        int iD = d(obj);
        if (iD >= 0) {
            return g(iD);
        }
        return null;
    }

    public final Object replace(Object obj, Object obj2) {
        int iD = d(obj);
        if (iD >= 0) {
            return h(iD, obj2);
        }
        return null;
    }

    public final int size() {
        return this.f57006e;
    }

    public final String toString() {
        if (isEmpty()) {
            return "{}";
        }
        StringBuilder sb2 = new StringBuilder(this.f57006e * 28);
        sb2.append('{');
        int i10 = this.f57006e;
        for (int i11 = 0; i11 < i10; i11++) {
            if (i11 > 0) {
                sb2.append(", ");
            }
            Object objF = f(i11);
            if (objF != sb2) {
                sb2.append(objF);
            } else {
                sb2.append("(this Map)");
            }
            sb2.append('=');
            Object objI = i(i11);
            if (objI != sb2) {
                sb2.append(objI);
            } else {
                sb2.append("(this Map)");
            }
        }
        sb2.append('}');
        String string = sb2.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        return string;
    }

    public final boolean remove(Object obj, Object obj2) {
        int iD = d(obj);
        if (iD < 0 || !Intrinsics.areEqual(obj2, i(iD))) {
            return false;
        }
        g(iD);
        return true;
    }

    public final boolean replace(Object obj, Object obj2, Object obj3) {
        int iD = d(obj);
        if (iD < 0 || !Intrinsics.areEqual(obj2, i(iD))) {
            return false;
        }
        h(iD, obj3);
        return true;
    }
}

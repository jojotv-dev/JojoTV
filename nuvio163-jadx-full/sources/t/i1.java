package t;

import java.util.Arrays;
import kotlin.collections.ArraysKt;
import kotlin.collections.ArraysKt___ArraysJvmKt;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class i1 implements Cloneable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public /* synthetic */ boolean f57009b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public /* synthetic */ int[] f57010c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public /* synthetic */ Object[] f57011e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public /* synthetic */ int f57012i;

    public i1(int i10) {
        int i11;
        int i12 = 4;
        while (true) {
            i11 = 40;
            if (i12 >= 32) {
                break;
            }
            int i13 = (1 << i12) - 12;
            if (40 <= i13) {
                i11 = i13;
                break;
            }
            i12++;
        }
        int i14 = i11 / 4;
        this.f57010c = new int[i14];
        this.f57011e = new Object[i14];
    }

    public final void a(int i10, Object obj) {
        int i11 = this.f57012i;
        if (i11 != 0 && i10 <= this.f57010c[i11 - 1]) {
            f(i10, obj);
            return;
        }
        if (this.f57009b && i11 >= this.f57010c.length) {
            a0.a(this);
        }
        int i12 = this.f57012i;
        if (i12 >= this.f57010c.length) {
            int i13 = (i12 + 1) * 4;
            for (int i14 = 4; i14 < 32; i14++) {
                int i15 = (1 << i14) - 12;
                if (i13 <= i15) {
                    i13 = i15;
                    break;
                }
            }
            int i16 = i13 / 4;
            int[] iArrCopyOf = Arrays.copyOf(this.f57010c, i16);
            Intrinsics.checkNotNullExpressionValue(iArrCopyOf, "copyOf(...)");
            this.f57010c = iArrCopyOf;
            Object[] objArrCopyOf = Arrays.copyOf(this.f57011e, i16);
            Intrinsics.checkNotNullExpressionValue(objArrCopyOf, "copyOf(...)");
            this.f57011e = objArrCopyOf;
        }
        this.f57010c[i12] = i10;
        this.f57011e[i12] = obj;
        this.f57012i = i12 + 1;
    }

    /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
    public final i1 clone() throws CloneNotSupportedException {
        Object objClone = super.clone();
        Intrinsics.checkNotNull(objClone, "null cannot be cast to non-null type androidx.collection.SparseArrayCompat<E of androidx.collection.SparseArrayCompat>");
        i1 i1Var = (i1) objClone;
        i1Var.f57010c = (int[]) this.f57010c.clone();
        i1Var.f57011e = (Object[]) this.f57011e.clone();
        return i1Var;
    }

    public final boolean c(int i10) {
        if (this.f57009b) {
            a0.a(this);
        }
        return u.a.a(this.f57012i, i10, this.f57010c) >= 0;
    }

    public final Object d(int i10) {
        Object obj;
        Intrinsics.checkNotNullParameter(this, "<this>");
        int iA = u.a.a(this.f57012i, i10, this.f57010c);
        if (iA < 0 || (obj = this.f57011e[iA]) == a0.f56944c) {
            return null;
        }
        return obj;
    }

    public final int e(int i10) {
        if (this.f57009b) {
            a0.a(this);
        }
        return this.f57010c[i10];
    }

    public final void f(int i10, Object obj) {
        int iA = u.a.a(this.f57012i, i10, this.f57010c);
        if (iA >= 0) {
            this.f57011e[iA] = obj;
            return;
        }
        int i11 = ~iA;
        int i12 = this.f57012i;
        if (i11 < i12) {
            Object[] objArr = this.f57011e;
            if (objArr[i11] == a0.f56944c) {
                this.f57010c[i11] = i10;
                objArr[i11] = obj;
                return;
            }
        }
        if (this.f57009b && i12 >= this.f57010c.length) {
            a0.a(this);
            i11 = ~u.a.a(this.f57012i, i10, this.f57010c);
        }
        int i13 = this.f57012i;
        if (i13 >= this.f57010c.length) {
            int i14 = (i13 + 1) * 4;
            for (int i15 = 4; i15 < 32; i15++) {
                int i16 = (1 << i15) - 12;
                if (i14 <= i16) {
                    i14 = i16;
                    break;
                }
            }
            int i17 = i14 / 4;
            int[] iArrCopyOf = Arrays.copyOf(this.f57010c, i17);
            Intrinsics.checkNotNullExpressionValue(iArrCopyOf, "copyOf(...)");
            this.f57010c = iArrCopyOf;
            Object[] objArrCopyOf = Arrays.copyOf(this.f57011e, i17);
            Intrinsics.checkNotNullExpressionValue(objArrCopyOf, "copyOf(...)");
            this.f57011e = objArrCopyOf;
        }
        int i18 = this.f57012i;
        if (i18 - i11 != 0) {
            int[] iArr = this.f57010c;
            int i19 = i11 + 1;
            ArraysKt___ArraysJvmKt.copyInto(iArr, iArr, i19, i11, i18);
            Object[] objArr2 = this.f57011e;
            ArraysKt.copyInto(objArr2, objArr2, i19, i11, this.f57012i);
        }
        this.f57010c[i11] = i10;
        this.f57011e[i11] = obj;
        this.f57012i++;
    }

    public final int g() {
        if (this.f57009b) {
            a0.a(this);
        }
        return this.f57012i;
    }

    public final Object h(int i10) {
        if (this.f57009b) {
            a0.a(this);
        }
        Object[] objArr = this.f57011e;
        if (i10 < objArr.length) {
            return objArr[i10];
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public final String toString() {
        if (g() <= 0) {
            return "{}";
        }
        StringBuilder sb2 = new StringBuilder(this.f57012i * 28);
        sb2.append('{');
        int i10 = this.f57012i;
        for (int i11 = 0; i11 < i10; i11++) {
            if (i11 > 0) {
                sb2.append(", ");
            }
            sb2.append(e(i11));
            sb2.append('=');
            Object objH = h(i11);
            if (objH != this) {
                sb2.append(objH);
            } else {
                sb2.append("(this Map)");
            }
        }
        sb2.append('}');
        String string = sb2.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        return string;
    }
}

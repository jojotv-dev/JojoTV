package t;

import java.util.List;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class m0 extends y0 {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public h1.b f57031c;

    public m0(int i10) {
        this.f57101a = i10 == 0 ? z0.f57107a : new Object[i10];
    }

    public final void g(Object obj) {
        int i10 = this.f57102b + 1;
        Object[] objArr = this.f57101a;
        if (objArr.length < i10) {
            n(i10, objArr);
        }
        Object[] objArr2 = this.f57101a;
        int i11 = this.f57102b;
        objArr2[i11] = obj;
        this.f57102b = i11 + 1;
    }

    public final void h(List elements) {
        Intrinsics.checkNotNullParameter(elements, "elements");
        Intrinsics.checkNotNullParameter(elements, "elements");
        if (elements.isEmpty()) {
            return;
        }
        int i10 = this.f57102b;
        int size = elements.size() + i10;
        Object[] objArr = this.f57101a;
        if (objArr.length < size) {
            n(size, objArr);
        }
        Object[] objArr2 = this.f57101a;
        int size2 = elements.size();
        for (int i11 = 0; i11 < size2; i11++) {
            objArr2[i11 + i10] = elements.get(i11);
        }
        this.f57102b = elements.size() + this.f57102b;
    }

    public final void i(y0 elements) {
        Intrinsics.checkNotNullParameter(elements, "elements");
        Intrinsics.checkNotNullParameter(elements, "elements");
        if (elements.d()) {
            return;
        }
        int i10 = this.f57102b + elements.f57102b;
        Object[] objArr = this.f57101a;
        if (objArr.length < i10) {
            n(i10, objArr);
        }
        ArraysKt.copyInto(elements.f57101a, this.f57101a, this.f57102b, 0, elements.f57102b);
        this.f57102b += elements.f57102b;
    }

    public final void j() {
        ArraysKt.fill(this.f57101a, (Object) null, 0, this.f57102b);
        this.f57102b = 0;
    }

    public final boolean k(Object obj) {
        int iC = c(obj);
        if (iC < 0) {
            return false;
        }
        l(iC);
        return true;
    }

    public final Object l(int i10) {
        int i11;
        if (i10 < 0 || i10 >= (i11 = this.f57102b)) {
            f(i10);
            throw null;
        }
        Object[] objArr = this.f57101a;
        Object obj = objArr[i10];
        if (i10 != i11 - 1) {
            ArraysKt.copyInto(objArr, objArr, i10, i10 + 1, i11);
        }
        int i12 = this.f57102b - 1;
        this.f57102b = i12;
        objArr[i12] = null;
        return obj;
    }

    public final void m(int i10, int i11) {
        int i12;
        if (i10 < 0 || i10 > (i12 = this.f57102b) || i11 < 0 || i11 > i12) {
            StringBuilder sbW = o2.a.w(i10, "Start (", ") and end (", ") must be in 0..", i11);
            sbW.append(this.f57102b);
            u.a.d(sbW.toString());
            throw null;
        }
        if (i11 < i10) {
            u.a.c("Start (" + i10 + ") is more than end (" + i11 + ')');
            throw null;
        }
        if (i11 != i10) {
            if (i11 < i12) {
                Object[] objArr = this.f57101a;
                ArraysKt.copyInto(objArr, objArr, i10, i11, i12);
            }
            int i13 = this.f57102b;
            int i14 = i13 - (i11 - i10);
            ArraysKt.fill(this.f57101a, (Object) null, i14, i13);
            this.f57102b = i14;
        }
    }

    public final void n(int i10, Object[] oldContent) {
        Intrinsics.checkNotNullParameter(oldContent, "oldContent");
        int length = oldContent.length;
        this.f57101a = ArraysKt.copyInto(oldContent, new Object[Math.max(i10, (length * 3) / 2)], 0, 0, length);
    }

    public final Object o(int i10, Object obj) {
        if (i10 < 0 || i10 >= this.f57102b) {
            f(i10);
            throw null;
        }
        Object[] objArr = this.f57101a;
        Object obj2 = objArr[i10];
        objArr[i10] = obj;
        return obj2;
    }

    public /* synthetic */ m0() {
        this(16);
    }
}

package androidx.recyclerview.widget;

import android.view.View;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes3.dex */
public final class n1 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final ArrayList f4005a = new ArrayList();

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public int f4006b = Integer.MIN_VALUE;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f4007c = Integer.MIN_VALUE;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public int f4008d = 0;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final int f4009e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public final /* synthetic */ StaggeredGridLayoutManager f4010f;

    public n1(StaggeredGridLayoutManager staggeredGridLayoutManager, int i10) {
        this.f4010f = staggeredGridLayoutManager;
        this.f4009e = i10;
    }

    public final void a() {
        View view = (View) h1.f.h(this.f4005a, 1);
        k1 k1Var = (k1) view.getLayoutParams();
        this.f4007c = this.f4010f.f3863r.b(view);
        k1Var.getClass();
    }

    public final void b() {
        this.f4005a.clear();
        this.f4006b = Integer.MIN_VALUE;
        this.f4007c = Integer.MIN_VALUE;
        this.f4008d = 0;
    }

    public final int c() {
        boolean z2 = this.f4010f.f3868w;
        ArrayList arrayList = this.f4005a;
        return z2 ? e(arrayList.size() - 1, -1) : e(0, arrayList.size());
    }

    public final int d() {
        boolean z2 = this.f4010f.f3868w;
        ArrayList arrayList = this.f4005a;
        return z2 ? e(0, arrayList.size()) : e(arrayList.size() - 1, -1);
    }

    public final int e(int i10, int i11) {
        StaggeredGridLayoutManager staggeredGridLayoutManager = this.f4010f;
        int iK = staggeredGridLayoutManager.f3863r.k();
        int iG = staggeredGridLayoutManager.f3863r.g();
        int i12 = i11 > i10 ? 1 : -1;
        while (i10 != i11) {
            View view = (View) this.f4005a.get(i10);
            int iE = staggeredGridLayoutManager.f3863r.e(view);
            int iB = staggeredGridLayoutManager.f3863r.b(view);
            boolean z2 = iE <= iG;
            boolean z10 = iB >= iK;
            if (z2 && z10 && (iE < iK || iB > iG)) {
                return p0.E(view);
            }
            i10 += i12;
        }
        return -1;
    }

    public final int f(int i10) {
        int i11 = this.f4007c;
        if (i11 != Integer.MIN_VALUE) {
            return i11;
        }
        if (this.f4005a.size() == 0) {
            return i10;
        }
        a();
        return this.f4007c;
    }

    public final View g(int i10, int i11) {
        StaggeredGridLayoutManager staggeredGridLayoutManager = this.f4010f;
        ArrayList arrayList = this.f4005a;
        View view = null;
        if (i11 != -1) {
            int size = arrayList.size() - 1;
            while (size >= 0) {
                View view2 = (View) arrayList.get(size);
                if ((staggeredGridLayoutManager.f3868w && p0.E(view2) >= i10) || ((!staggeredGridLayoutManager.f3868w && p0.E(view2) <= i10) || !view2.hasFocusable())) {
                    break;
                }
                size--;
                view = view2;
            }
            return view;
        }
        int size2 = arrayList.size();
        int i12 = 0;
        while (i12 < size2) {
            View view3 = (View) arrayList.get(i12);
            if ((staggeredGridLayoutManager.f3868w && p0.E(view3) <= i10) || ((!staggeredGridLayoutManager.f3868w && p0.E(view3) >= i10) || !view3.hasFocusable())) {
                break;
            }
            i12++;
            view = view3;
        }
        return view;
    }

    public final int h(int i10) {
        int i11 = this.f4006b;
        if (i11 != Integer.MIN_VALUE) {
            return i11;
        }
        ArrayList arrayList = this.f4005a;
        if (arrayList.size() == 0) {
            return i10;
        }
        View view = (View) arrayList.get(0);
        k1 k1Var = (k1) view.getLayoutParams();
        this.f4006b = this.f4010f.f3863r.e(view);
        k1Var.getClass();
        return this.f4006b;
    }
}

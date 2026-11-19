package n7;

import android.graphics.Matrix;
import java.util.HashSet;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public abstract class a0 extends x0 implements c0, u0 {

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public HashSet f39554i = null;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public String f39555j = null;
    public HashSet k = null;

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    public HashSet f39556l = null;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public HashSet f39557m = null;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public Matrix f39558n;

    @Override // n7.u0
    public final Set a() {
        return this.k;
    }

    @Override // n7.u0
    public final String b() {
        return this.f39555j;
    }

    @Override // n7.u0
    public final void d(HashSet hashSet) {
        this.f39554i = hashSet;
    }

    @Override // n7.u0
    public final Set e() {
        return this.f39554i;
    }

    @Override // n7.u0
    public final void f(HashSet hashSet) {
        this.k = hashSet;
    }

    @Override // n7.u0
    public final void g(HashSet hashSet) {
        this.f39557m = hashSet;
    }

    @Override // n7.u0
    public final void h(String str) {
        this.f39555j = str;
    }

    @Override // n7.u0
    public final void i(HashSet hashSet) {
        this.f39556l = hashSet;
    }

    @Override // n7.c0
    public final void k(Matrix matrix) {
        this.f39558n = matrix;
    }

    @Override // n7.u0
    public final Set l() {
        return this.f39556l;
    }

    @Override // n7.u0
    public final Set m() {
        return this.f39557m;
    }
}

package n7;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public abstract class v0 extends x0 implements w0, u0 {

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public List f39756i = new ArrayList();

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public HashSet f39757j = null;
    public String k = null;

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    public HashSet f39758l = null;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public HashSet f39759m = null;

    @Override // n7.u0
    public final Set a() {
        return null;
    }

    @Override // n7.u0
    public final String b() {
        return this.k;
    }

    @Override // n7.u0
    public final void d(HashSet hashSet) {
        this.f39757j = hashSet;
    }

    @Override // n7.u0
    public final Set e() {
        return this.f39757j;
    }

    @Override // n7.u0
    public final void g(HashSet hashSet) {
        this.f39759m = hashSet;
    }

    @Override // n7.w0
    public final List getChildren() {
        return this.f39756i;
    }

    @Override // n7.u0
    public final void h(String str) {
        this.k = str;
    }

    @Override // n7.u0
    public final void i(HashSet hashSet) {
        this.f39758l = hashSet;
    }

    public void j(a1 a1Var) {
        this.f39756i.add(a1Var);
    }

    @Override // n7.u0
    public final Set l() {
        return this.f39758l;
    }

    @Override // n7.u0
    public final Set m() {
        return this.f39759m;
    }

    @Override // n7.u0
    public final void f(HashSet hashSet) {
    }
}

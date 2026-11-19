package androidx.datastore.preferences.protobuf;

import kotlin.Unit;
import kotlin.collections.ArrayDeque;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public abstract class k {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public int f2500a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public Object f2501b = new ArrayDeque();

    public abstract void a(int i10);

    public abstract int b();

    public abstract boolean c();

    public abstract void d(int i10);

    public abstract int e(int i10);

    public abstract boolean f();

    public abstract g g();

    public abstract double h();

    public abstract int i();

    public abstract int j();

    public abstract long k();

    public abstract float l();

    public abstract int m();

    public abstract long n();

    public abstract int o();

    public abstract long p();

    public abstract int q();

    public abstract long r();

    public abstract String s();

    public abstract String t();

    public abstract int u();

    public abstract int v();

    public abstract long w();

    public void x(char[] array) {
        Intrinsics.checkNotNullParameter(array, "array");
        synchronized (this) {
            try {
                int i10 = this.f2500a;
                if (array.length + i10 < pi.e.f45110a) {
                    this.f2500a = i10 + array.length;
                    ((ArrayDeque) this.f2501b).addLast(array);
                }
                Unit unit = Unit.INSTANCE;
            } catch (Throwable th2) {
                throw th2;
            }
        }
    }

    public abstract boolean y(int i10);

    public char[] z(int i10) {
        char[] cArr;
        synchronized (this) {
            cArr = (char[]) ((ArrayDeque) this.f2501b).removeLastOrNull();
            if (cArr != null) {
                this.f2500a -= cArr.length;
            } else {
                cArr = null;
            }
        }
        return cArr == null ? new char[i10] : cArr;
    }
}

package n7;

import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public final class s0 implements Cloneable {
    public String A;
    public Boolean B;
    public Boolean C;
    public b1 D;
    public Float E;
    public String F;
    public String G;
    public b1 H;
    public Float I;

    /* JADX INFO: renamed from: J, reason: collision with root package name */
    public b1 f39716J;
    public Float K;
    public int L;
    public int M;
    public int N;
    public int O;
    public int P;
    public int Q;
    public int R;
    public int S;
    public int T;
    public int U;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public long f39717b = 0;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public b1 f39718c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public Float f39719e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public b1 f39720i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public Float f39721m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public e0 f39722n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public Float f39723o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public e0[] f39724p;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public e0 f39725q;

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    public Float f39726r;

    /* JADX INFO: renamed from: s, reason: collision with root package name */
    public v f39727s;

    /* JADX INFO: renamed from: t, reason: collision with root package name */
    public ArrayList f39728t;

    /* JADX INFO: renamed from: u, reason: collision with root package name */
    public e0 f39729u;

    /* JADX INFO: renamed from: v, reason: collision with root package name */
    public Integer f39730v;

    /* JADX INFO: renamed from: w, reason: collision with root package name */
    public Boolean f39731w;

    /* JADX INFO: renamed from: x, reason: collision with root package name */
    public g5.e f39732x;

    /* JADX INFO: renamed from: y, reason: collision with root package name */
    public String f39733y;

    /* JADX INFO: renamed from: z, reason: collision with root package name */
    public String f39734z;

    public static s0 a() {
        s0 s0Var = new s0();
        s0Var.f39717b = -1L;
        v vVar = v.f39753c;
        s0Var.f39718c = vVar;
        s0Var.L = 1;
        Float fValueOf = Float.valueOf(1.0f);
        s0Var.f39719e = fValueOf;
        s0Var.f39720i = null;
        s0Var.f39721m = fValueOf;
        s0Var.f39722n = new e0(1.0f);
        s0Var.M = 1;
        s0Var.N = 1;
        s0Var.f39723o = Float.valueOf(4.0f);
        s0Var.f39724p = null;
        s0Var.f39725q = new e0(0.0f);
        s0Var.f39726r = fValueOf;
        s0Var.f39727s = vVar;
        s0Var.f39728t = null;
        s0Var.f39729u = new e0(12.0f, 7);
        s0Var.f39730v = 400;
        s0Var.O = 1;
        s0Var.P = 1;
        s0Var.Q = 1;
        s0Var.R = 1;
        Boolean bool = Boolean.TRUE;
        s0Var.f39731w = bool;
        s0Var.f39732x = null;
        s0Var.f39733y = null;
        s0Var.f39734z = null;
        s0Var.A = null;
        s0Var.B = bool;
        s0Var.C = bool;
        s0Var.D = vVar;
        s0Var.E = fValueOf;
        s0Var.F = null;
        s0Var.S = 1;
        s0Var.G = null;
        s0Var.H = null;
        s0Var.I = fValueOf;
        s0Var.f39716J = null;
        s0Var.K = fValueOf;
        s0Var.T = 1;
        s0Var.U = 1;
        return s0Var;
    }

    public final Object clone() {
        s0 s0Var = (s0) super.clone();
        e0[] e0VarArr = this.f39724p;
        if (e0VarArr != null) {
            s0Var.f39724p = (e0[]) e0VarArr.clone();
        }
        return s0Var;
    }
}

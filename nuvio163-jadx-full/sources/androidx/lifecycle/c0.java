package androidx.lifecycle;

/* JADX INFO: loaded from: classes.dex */
public abstract class c0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final f0 f2626a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public boolean f2627b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f2628c = -1;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final /* synthetic */ d0 f2629d;

    public c0(d0 d0Var, f0 f0Var) {
        this.f2629d = d0Var;
        this.f2626a = f0Var;
    }

    public final void a(boolean z2) {
        if (z2 == this.f2627b) {
            return;
        }
        this.f2627b = z2;
        int i10 = z2 ? 1 : -1;
        d0 d0Var = this.f2629d;
        int i11 = d0Var.f2636c;
        d0Var.f2636c = i10 + i11;
        if (!d0Var.f2637d) {
            d0Var.f2637d = true;
            while (true) {
                try {
                    int i12 = d0Var.f2636c;
                    if (i11 == i12) {
                        break;
                    } else {
                        i11 = i12;
                    }
                } catch (Throwable th2) {
                    d0Var.f2637d = false;
                    throw th2;
                }
            }
            d0Var.f2637d = false;
        }
        if (this.f2627b) {
            d0Var.b(this);
        }
    }
}

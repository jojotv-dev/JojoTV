package f4;

/* JADX INFO: loaded from: classes3.dex */
public final class d {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public boolean f16183a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public c f16184b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public boolean f16185c;

    public final void a() {
        synchronized (this) {
            try {
                if (this.f16183a) {
                    return;
                }
                this.f16183a = true;
                this.f16185c = true;
                c cVar = this.f16184b;
                if (cVar != null) {
                    try {
                        cVar.onCancel();
                    } catch (Throwable th2) {
                        synchronized (this) {
                            this.f16185c = false;
                            notifyAll();
                            throw th2;
                        }
                    }
                }
                synchronized (this) {
                    this.f16185c = false;
                    notifyAll();
                }
            } catch (Throwable th3) {
                throw th3;
            }
        }
    }

    public final void b(c cVar) {
        synchronized (this) {
            while (this.f16185c) {
                try {
                    try {
                        wait();
                    } catch (InterruptedException unused) {
                    }
                } catch (Throwable th2) {
                    throw th2;
                }
            }
            if (this.f16184b == cVar) {
                return;
            }
            this.f16184b = cVar;
            if (this.f16183a) {
                cVar.onCancel();
            }
        }
    }
}

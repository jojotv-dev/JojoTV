package qj;

import java.io.InputStream;

/* JADX INFO: loaded from: classes5.dex */
public abstract class v1 extends InputStream {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final InputStream f50910b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f50911c;

    public v1(InputStream inputStream, int i10) {
        this.f50910b = inputStream;
        this.f50911c = i10;
    }

    public final void g() {
        InputStream inputStream = this.f50910b;
        if (inputStream instanceof s1) {
            s1 s1Var = (s1) inputStream;
            s1Var.f50898n = true;
            s1Var.l();
        }
    }
}

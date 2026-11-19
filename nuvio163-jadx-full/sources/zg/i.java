package zg;

import java.security.PrivateKey;
import java.security.cert.Certificate;

/* JADX INFO: loaded from: classes5.dex */
public final class i {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final PrivateKey f66745a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Certificate f66746b;

    public i(PrivateKey privateKey, Certificate certificate) {
        this.f66745a = privateKey;
        this.f66746b = certificate;
    }

    public final void a() {
        try {
            this.f66745a.destroy();
        } catch (NoSuchMethodError unused) {
        }
    }
}

package q9;

import javax.crypto.spec.GCMParameterSpec;

/* JADX INFO: loaded from: classes4.dex */
public final class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final kk.a f45776a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f45777b;

    public a(kk.a aVar, int i10) {
        this.f45777b = i10;
        this.f45776a = aVar;
    }

    public final nk.a a(byte[] bArr, GCMParameterSpec gCMParameterSpec) {
        switch (this.f45777b) {
            case 0:
                break;
        }
        return new nk.a(new nk.h(bArr), gCMParameterSpec.getTLen(), gCMParameterSpec.getIV(), null);
    }
}

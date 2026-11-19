package kf;

import kotlin.jvm.functions.Function0;
import okhttp3.Handshake;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class jb implements Function0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f29542b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Function0 f29543c;

    public /* synthetic */ jb(int i10, Function0 function0) {
        this.f29542b = i10;
        this.f29543c = function0;
    }

    @Override // kotlin.jvm.functions.Function0
    public final Object invoke() {
        boolean z2;
        boolean z10;
        switch (this.f29542b) {
            case 0:
                Boolean bool = (Boolean) this.f29543c.invoke();
                bool.booleanValue();
                return bool;
            case 1:
                return (String) this.f29543c.invoke();
            case 2:
                Boolean bool2 = (Boolean) this.f29543c.invoke();
                bool2.booleanValue();
                return bool2;
            case 3:
                return Handshake.peerCertificates_delegate$lambda$0(this.f29543c);
            case 4:
                Function0 function0 = this.f29543c;
                if (function0 != null) {
                    function0.invoke();
                    z2 = true;
                } else {
                    z2 = false;
                }
                return Boolean.valueOf(z2);
            case 5:
                Function0 function1 = this.f29543c;
                if (function1 != null) {
                    function1.invoke();
                    z10 = true;
                } else {
                    z10 = false;
                }
                return Boolean.valueOf(z10);
            default:
                this.f29543c.invoke();
                return Boolean.TRUE;
        }
    }
}

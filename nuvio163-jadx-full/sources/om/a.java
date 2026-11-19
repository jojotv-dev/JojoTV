package om;

import java.security.PrivilegedAction;
import java.security.Security;

/* JADX INFO: loaded from: classes5.dex */
public final class a implements PrivilegedAction {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f43899a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ String f43900b;

    public /* synthetic */ a(String str, int i10) {
        this.f43899a = i10;
        this.f43900b = str;
    }

    @Override // java.security.PrivilegedAction
    public final Object run() {
        switch (this.f43899a) {
            case 0:
                try {
                    return Class.forName(this.f43900b);
                } catch (Exception unused) {
                    return null;
                }
            case 1:
                try {
                    return ClassLoader.getSystemClassLoader().loadClass(this.f43900b);
                } catch (Exception unused2) {
                    return null;
                }
            case 2:
                return Security.getProperty(this.f43900b);
            default:
                return System.getProperty(this.f43900b);
        }
    }
}

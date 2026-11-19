package ua;

import com.liskovsoft.googleapi.oauth2.impl.GoogleSignInService;
import java.util.concurrent.Callable;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class b implements Callable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f58332b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ GoogleSignInService f58333c;

    public /* synthetic */ b(GoogleSignInService googleSignInService, int i10) {
        this.f58332b = i10;
        this.f58333c = googleSignInService;
    }

    @Override // java.util.concurrent.Callable
    public final Object call() {
        switch (this.f58332b) {
            case 0:
                return this.f58333c.getAccounts();
            default:
                return Boolean.valueOf(this.f58333c.isSigned());
        }
    }
}

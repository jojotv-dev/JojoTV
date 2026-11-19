package ua;

import com.liskovsoft.googleapi.oauth2.impl.GoogleSignInService;
import eh.d;
import eh.e;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class a implements e {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f58330b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ GoogleSignInService f58331c;

    public /* synthetic */ a(GoogleSignInService googleSignInService, int i10) {
        this.f58330b = i10;
        this.f58331c = googleSignInService;
    }

    @Override // eh.e
    public final void a(d dVar) {
        switch (this.f58330b) {
            case 0:
                this.f58331c.lambda$signInObserve$0(dVar);
                break;
            default:
                this.f58331c.lambda$signOutObserve$1(dVar);
                break;
        }
    }
}

package qe;

import android.app.Activity;
import android.view.WindowManager;

/* JADX INFO: loaded from: classes4.dex */
public final class q6 implements g1.c0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ g1.n1 f49772a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ Activity f49773b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ int f49774c;

    public q6(g1.n1 n1Var, Activity activity, int i10) {
        this.f49772a = n1Var;
        this.f49773b = activity;
        this.f49774c = i10;
    }

    @Override // g1.c0
    public final void dispose() {
        if (((Boolean) this.f49772a.getValue()).booleanValue()) {
            Activity activity = this.f49773b;
            WindowManager.LayoutParams attributes = activity.getWindow().getAttributes();
            int i10 = attributes.preferredDisplayModeId;
            int i11 = this.f49774c;
            if (i10 != i11) {
                attributes.preferredDisplayModeId = i11;
                activity.getWindow().setAttributes(attributes);
            }
        }
    }
}

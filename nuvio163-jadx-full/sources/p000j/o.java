package p000j;

import android.window.OnBackInvokedCallback;
import kotlin.jvm.functions.Function0;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class o implements OnBackInvokedCallback {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f26845a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ Object f26846b;

    public /* synthetic */ o(Object obj, int i10) {
        this.f26845a = i10;
        this.f26846b = obj;
    }

    public final void onBackInvoked() {
        switch (this.f26845a) {
            case 0:
                ((v) this.f26846b).r();
                break;
            case 1:
                ((n5.o) this.f26846b).b();
                break;
            case 2:
                ((Runnable) this.f26846b).run();
                break;
            default:
                Function0 function0 = (Function0) this.f26846b;
                if (function0 != null) {
                    function0.invoke();
                }
                break;
        }
    }
}

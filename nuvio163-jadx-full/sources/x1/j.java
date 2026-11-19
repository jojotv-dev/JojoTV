package x1;

import android.view.View;
import android.view.autofill.AutofillManager;

/* JADX INFO: loaded from: classes3.dex */
public abstract class j {
    public static void a(View view, AutofillManager autofillManager, int i10, boolean z2) {
        autofillManager.notifyViewVisibilityChanged(view, i10, z2);
    }
}

package k4;

import android.view.View;

/* JADX INFO: loaded from: classes.dex */
public abstract class n0 {
    public static int a(View view) {
        return view.getImportantForAutofill();
    }

    public static void b(View view, int i10) {
        view.setImportantForAutofill(i10);
    }
}

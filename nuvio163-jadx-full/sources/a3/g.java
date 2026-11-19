package a3;

import android.graphics.Outline;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import d2.l;
import d2.u0;

/* JADX INFO: loaded from: classes.dex */
public abstract class g {
    public static CharSequence a(AccessibilityNodeInfo accessibilityNodeInfo) {
        return accessibilityNodeInfo.getStateDescription();
    }

    public static void b(View view) {
        view.setImportantForContentCapture(1);
    }

    public static void c(Outline outline, u0 u0Var) {
        if (!(u0Var instanceof l)) {
            throw new UnsupportedOperationException("Unable to obtain android.graphics.Path");
        }
        outline.setPath(((l) u0Var).f11742a);
    }

    public static void d(AccessibilityNodeInfo accessibilityNodeInfo, CharSequence charSequence) {
        accessibilityNodeInfo.setStateDescription(charSequence);
    }
}

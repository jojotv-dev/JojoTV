package k4;

import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;
import android.widget.TextView;

/* JADX INFO: loaded from: classes3.dex */
public abstract class d0 {
    public static float a(VelocityTracker velocityTracker, int i10) {
        return velocityTracker.getAxisVelocity(i10);
    }

    public static int b(ViewConfiguration viewConfiguration, int i10, int i11, int i12) {
        return viewConfiguration.getScaledMaximumFlingVelocity(i10, i11, i12);
    }

    public static int c(ViewConfiguration viewConfiguration, int i10, int i11, int i12) {
        return viewConfiguration.getScaledMinimumFlingVelocity(i10, i11, i12);
    }

    public static void d(AccessibilityEvent accessibilityEvent, boolean z2) {
        accessibilityEvent.setAccessibilityDataSensitive(z2);
    }

    public static void e(TextView textView, int i10, float f10) {
        textView.setLineHeight(i10, f10);
    }
}

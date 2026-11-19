package androidx.recyclerview.widget;

import android.view.View;

/* JADX INFO: loaded from: classes3.dex */
public abstract class i0 {
    public static void a(View view, float f10) {
        try {
            view.setFrameContentVelocity(f10);
        } catch (LinkageError unused) {
        }
    }
}

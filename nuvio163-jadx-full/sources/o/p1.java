package o;

import android.widget.AbsListView;

/* JADX INFO: loaded from: classes3.dex */
public abstract class p1 {
    public static boolean a(AbsListView absListView) {
        return absListView.isSelectedChildViewEnabled();
    }

    public static void b(AbsListView absListView, boolean z2) {
        absListView.setSelectedChildViewEnabled(z2);
    }
}

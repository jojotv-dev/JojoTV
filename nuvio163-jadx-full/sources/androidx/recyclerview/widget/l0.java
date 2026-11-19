package androidx.recyclerview.widget;

import android.view.View;

/* JADX INFO: loaded from: classes.dex */
public final class l0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public int f3978a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public int f3979b;

    public /* synthetic */ l0(int i10, int i11) {
        this.f3978a = i10;
        this.f3979b = i11;
    }

    public void a(g1 g1Var) {
        View view = g1Var.itemView;
        this.f3978a = view.getLeft();
        this.f3979b = view.getTop();
        view.getRight();
        view.getBottom();
    }
}

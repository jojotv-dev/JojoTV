package n;

import android.view.CollapsibleActionView;
import android.view.View;
import android.widget.FrameLayout;

/* JADX INFO: loaded from: classes3.dex */
public final class p extends FrameLayout implements m.b {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final CollapsibleActionView f39388b;

    /* JADX WARN: Multi-variable type inference failed */
    public p(View view) {
        super(view.getContext());
        this.f39388b = (CollapsibleActionView) view;
        addView(view);
    }

    public final void a() {
        this.f39388b.onActionViewCollapsed();
    }

    public final void b() {
        this.f39388b.onActionViewExpanded();
    }
}

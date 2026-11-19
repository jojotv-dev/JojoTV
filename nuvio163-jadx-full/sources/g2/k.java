package g2;

import android.graphics.RenderNode;
import android.view.View;
import d2.x0;

/* JADX INFO: loaded from: classes.dex */
public abstract class k {
    public static void a(RenderNode renderNode, x0 x0Var) {
        renderNode.setRenderEffect(x0Var != null ? x0Var.a() : null);
    }

    public static void b(View view, x0 x0Var) {
        view.setRenderEffect(x0Var != null ? x0Var.a() : null);
    }
}

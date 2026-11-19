package o;

import android.text.StaticLayout;
import android.widget.TextView;

/* JADX INFO: loaded from: classes.dex */
public final class d1 extends c1 {
    @Override // o.c1, o.e1
    public void a(StaticLayout.Builder builder, TextView textView) {
        builder.setTextDirection(textView.getTextDirectionHeuristic());
    }

    @Override // o.e1
    public boolean b(TextView textView) {
        return textView.isHorizontallyScrollable();
    }
}

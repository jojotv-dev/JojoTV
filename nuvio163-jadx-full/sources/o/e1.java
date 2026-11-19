package o;

import android.text.StaticLayout;
import android.widget.TextView;

/* JADX INFO: loaded from: classes.dex */
public abstract class e1 {
    public abstract void a(StaticLayout.Builder builder, TextView textView);

    public boolean b(TextView textView) {
        return ((Boolean) f1.e(textView, "getHorizontallyScrolling", Boolean.FALSE)).booleanValue();
    }
}

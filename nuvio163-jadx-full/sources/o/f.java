package o;

import android.content.Context;
import android.view.View;

/* JADX INFO: loaded from: classes3.dex */
public final class f extends View {
    public f(Context context) {
        super(context);
        setWillNotDraw(true);
    }

    @Override // android.view.View
    public final int getWindowSystemUiVisibility() {
        return 0;
    }
}

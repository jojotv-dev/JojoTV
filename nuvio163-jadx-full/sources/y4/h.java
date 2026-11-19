package y4;

import android.os.Handler;
import android.widget.EditText;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes3.dex */
public final class h extends w4.h implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final WeakReference f64631b;

    public h(EditText editText) {
        this.f64631b = new WeakReference(editText);
    }

    @Override // w4.h
    public final void b() {
        Handler handler;
        EditText editText = (EditText) this.f64631b.get();
        if (editText == null || (handler = editText.getHandler()) == null) {
            return;
        }
        handler.post(this);
    }

    @Override // java.lang.Runnable
    public final void run() {
        i.a((EditText) this.f64631b.get(), 1);
    }
}

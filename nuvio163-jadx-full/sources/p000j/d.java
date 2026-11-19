package p000j;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes3.dex */
public final class d extends Handler {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public WeakReference f26788a;

    @Override // android.os.Handler
    public final void handleMessage(Message message) {
        int i10 = message.what;
        if (i10 == -3 || i10 == -2 || i10 == -1) {
            ((DialogInterface.OnClickListener) message.obj).onClick((DialogInterface) this.f26788a.get(), message.what);
        } else {
            if (i10 != 1) {
                return;
            }
            ((DialogInterface) message.obj).dismiss();
        }
    }
}

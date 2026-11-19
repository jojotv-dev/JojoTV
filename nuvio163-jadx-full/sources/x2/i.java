package x2;

import android.content.ClipboardManager;
import android.content.Context;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class i implements e1 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Context f63096a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public ClipboardManager f63097b;

    public i(Context context) {
        this.f63096a = context;
    }

    public final ClipboardManager a() {
        ClipboardManager clipboardManager = this.f63097b;
        if (clipboardManager != null) {
            return clipboardManager;
        }
        Object systemService = this.f63096a.getSystemService("clipboard");
        Intrinsics.checkNotNull(systemService, "null cannot be cast to non-null type android.content.ClipboardManager");
        ClipboardManager clipboardManager2 = (ClipboardManager) systemService;
        this.f63097b = clipboardManager2;
        return clipboardManager2;
    }
}

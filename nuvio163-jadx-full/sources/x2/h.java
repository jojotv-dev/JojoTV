package x2;

import android.content.ClipData;
import android.os.Build;
import kotlin.Unit;

/* JADX INFO: loaded from: classes.dex */
public final class h implements d1 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final i f63086a;

    public h(i iVar) {
        this.f63086a = iVar;
    }

    public final Unit a(c1 c1Var) {
        i iVar = this.f63086a;
        if (c1Var != null) {
            iVar.a().setPrimaryClip(c1Var.a());
        } else if (Build.VERSION.SDK_INT >= 28) {
            w0.a(iVar.a());
        } else {
            iVar.a().setPrimaryClip(ClipData.newPlainText("", ""));
        }
        return Unit.INSTANCE;
    }
}
